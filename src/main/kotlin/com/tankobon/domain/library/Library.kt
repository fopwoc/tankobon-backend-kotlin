package com.tankobon.domain.library

import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.MangaServiceProvider
import com.tankobon.domain.providers.TaskQueueProvider
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
import java.util.UUID

private const val TASK_DELAY = 1000L
private const val TASK_DEBOUNCE = 1000L * 10

class Library {
    companion object {
        val log by injectLogger()
    }

    private fun pathRecursion(file: File): File {
        return if (file.toPath().nameCount - ConfigProvider.get().library.mangaFile.toPath().nameCount > 1) {
            pathRecursion(file.parentFile)
        } else {
            file
        }
    }

    @DelicateCoroutinesApi
    fun watchLibrary() {
        val mangaFile = ConfigProvider.get().library.mangaFile
        mangaFile.mkdirs()

        val taskQueue = TaskQueueProvider.get()

        runBlocking {
            GlobalScope.launch { taskQueue.runQueue() }

            val listFiles = mangaFile.listFiles()?.filter {
                !it.name.contains(".DS_Store")
            } ?: emptyList()

            MangaServiceProvider.get().cleanupMangaByIds(
                listFiles.mapNotNull {
                    uuidFromString(it.nameWithoutExtension)
                }
            )

            listFiles.forEach { e ->
                if (!e.name.contains(".DS_Store")) {
                    taskQueue.submit(
                        Task(
                            file = e,
                            id = uuidFromString(e.nameWithoutExtension) ?: UUID.randomUUID(),
                            state = TaskState.WAITING,
                            lastUpdate = System.currentTimeMillis()
                        )
                    )
                }
            }

            while (taskQueue.getCount() != 0) {
                delay(TASK_DELAY)
            }

            GlobalScope.launch(newSingleThreadContext("LibraryThread")) {
                while (true) {
                    log.debug("START FILE EVENT WATCH CHANNEL")
                    mangaFile.asWatchChannel(mode = KWatchChannel.Mode.Recursive).consumeEach { event ->
                        val eventNameCount = event.file.toPath().nameCount
                        val libraryNameCount = ConfigProvider.get().library.mangaFile.toPath().nameCount

                        if (eventNameCount - libraryNameCount > 0 && !event.file.name.contains(".DS_Store")) {
                            val file = pathRecursion(event.file)

                            log.trace("watch channel event $event")

                            taskQueue.submit(
                                Task(
                                    file = file,
                                    id = uuidFromString(file.name) ?: UUID.randomUUID(),
                                    state = TaskState.WAITING,
                                    lastUpdate = System.currentTimeMillis() + TASK_DEBOUNCE,
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
