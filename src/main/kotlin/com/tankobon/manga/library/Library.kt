package com.tankobon.manga.library

import com.tankobon.globalMangaPath
import com.tankobon.utils.KWatchChannel
import com.tankobon.utils.asWatchChannel
import com.tankobon.utils.injectLogger
import com.tankobon.utils.uuidFromString
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Path
import java.util.UUID

class Library {
    companion object { val log by injectLogger() }

    private fun pathRecursion(file: File): File {
        return if (file.toPath().nameCount > 2) {
            pathRecursion(file.parentFile)
        } else {
            file
        }
    }

    @DelicateCoroutinesApi
    fun watchLibrary() {
        val mangaFile = Path.of(globalMangaPath.path).toFile()
        mangaFile.mkdirs()

        val taskQueue = TaskQueue()

        runBlocking {
            GlobalScope.launch { taskQueue.runQueue() }

            // before start service recalculate all items in library
            mangaFile.listFiles()?.forEach { e ->
                if (!e.name.contains(".DS_Store")) {
                    taskQueue.submit(
                        Task(
                            file = e,
                            uuid = uuidFromString(e.name) ?: UUID.randomUUID(),
                            state = TaskState.WAITING,
                            lastUpdate = System.currentTimeMillis()
                        )
                    )
                }
            }

            while (taskQueue.getCount() != 0) {
                delay(1000L)
            }

            GlobalScope.launch(newSingleThreadContext("LibraryThread")) {
                while (true) {
                    log.debug("START FILE EVENT WATCH CHANNEL")
                    mangaFile.asWatchChannel(mode = KWatchChannel.Mode.Recursive).consumeEach { event ->
                        if (event.file.toPath().nameCount >= 2 && !event.file.name.contains(".DS_Store")) {
                            val file = pathRecursion(event.file)

                            log.trace("watch channel event $event")

                            taskQueue.submit(
                                Task(
                                    file = file,
                                    uuid = uuidFromString(file.name) ?: UUID.randomUUID(),
                                    state = TaskState.WAITING,
                                    lastUpdate = System.currentTimeMillis() + 10000L,
                                )
                            )
                        }
                    }
                    log.debug("PROBABLY, WATCH CHANNEL DIED, REVIVING")
                }
            }
        }
    }
}
