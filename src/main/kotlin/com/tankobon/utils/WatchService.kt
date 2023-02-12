// original code taken from https://github.com/vishna/watchservice-ktx/
// ty so much for this piece of code :>

package com.tankobon.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.nio.file.attribute.BasicFileAttributes

data class KWatchEvent(
    val file: File,
    val kind: Kind,
    val tag: Any?,
) {
    enum class Kind(val kind: String) {
        Initialized("initialized"),
        Created("created"),
        Modified("modified"),
        Deleted("deleted")
    }
}

@DelicateCoroutinesApi
fun File.asWatchChannel(
    mode: KWatchChannel.Mode? = null,
    tag: Any? = null,
    scope: CoroutineScope = GlobalScope,
) = KWatchChannel(
    file = this,
    mode = mode ?: if (isFile) KWatchChannel.Mode.SingleFile else KWatchChannel.Mode.Recursive,
    scope = scope,
    tag = tag
)

@DelicateCoroutinesApi
class KWatchChannel(
    val file: File,
    val scope: CoroutineScope = GlobalScope,
    val mode: Mode,
    val tag: Any? = null,
    private val channel: Channel<KWatchEvent> = Channel(),
) : Channel<KWatchEvent> by channel {
    val log = logger("watch-channel")

    private val watchService: WatchService = FileSystems.getDefault().newWatchService()
    private val registeredKeys = ArrayList<WatchKey>()
    private val path: Path = if (file.isFile) {
        file.parentFile
    } else {
        file
    }.toPath()

    private fun registerPaths() {
        registeredKeys.apply {
            forEach { it.cancel() }
            clear()
        }
        if (mode == Mode.Recursive) {
            Files.walkFileTree(
                path,
                object : SimpleFileVisitor<Path>() {
                    override fun preVisitDirectory(subPath: Path, attrs: BasicFileAttributes): FileVisitResult {
                        registeredKeys += subPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
                        return FileVisitResult.CONTINUE
                    }
                }
            )
        } else {
            registeredKeys += path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
        }
    }

    init {
        scope.launch(Dispatchers.IO) {
            channel.send(
                KWatchEvent(
                    file = path.toFile(),
                    tag = tag,
                    kind = KWatchEvent.Kind.Initialized
                )
            )

            var shouldRegisterPath = true

            while (!isClosedForSend) {

                try {
                    if (shouldRegisterPath) {
                        registerPaths()
                        shouldRegisterPath = false
                    }
                } catch (e: NoSuchFileException) {
                    log.debug("caught no file in path register")
                    return@launch
                }

                val monitorKey = watchService.take()
                val dirPath = monitorKey.watchable() as? Path ?: break
                monitorKey.pollEvents().forEach {
                    if (it.context() == null) {
                        log.debug("caught null in events polling")
                        return@forEach
                    }

                    val eventPath = dirPath.resolve(it.context() as Path)

                    log.trace("event for $eventPath, kind ${it.kind()}")

                    if (mode == Mode.SingleFile && eventPath.toFile().absolutePath != file.absolutePath) {
                        return@forEach
                    }

                    val eventType = when (it.kind()) {
                        ENTRY_CREATE -> KWatchEvent.Kind.Created
                        ENTRY_DELETE -> KWatchEvent.Kind.Deleted
                        else -> KWatchEvent.Kind.Modified
                    }

                    val event = KWatchEvent(
                        file = eventPath.toFile(),
                        tag = tag,
                        kind = eventType
                    )

                    if (mode == Mode.Recursive &&
                        event.kind in listOf(KWatchEvent.Kind.Created, KWatchEvent.Kind.Deleted) &&
                        event.file.isDirectory
                    ) {
                        shouldRegisterPath = true
                    }

                    channel.send(event)
                }

                if (!monitorKey.reset()) {
                    monitorKey.cancel()
                    close()
                    break
                } else if (isClosedForSend) {
                    break
                }
            }
        }
    }

    override fun close(cause: Throwable?): Boolean {
        registeredKeys.apply {
            forEach { it.cancel() }
            clear()
        }

        return channel.close(cause)
    }

    enum class Mode {
        SingleFile,
        SingleDirectory,
        Recursive
    }
}
