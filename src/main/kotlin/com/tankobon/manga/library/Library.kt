package com.tankobon.manga.library

import com.tankobon.globalMangaPath
import com.tankobon.utils.asWatchChannel
import com.tankobon.utils.injectLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Path

class Library {
    companion object {
        val log by injectLogger()
    }

    private fun pathRecursion(file: File): File {
        return if (file.toPath().nameCount > 2) {
            pathRecursion(file.parentFile)
        } else {
            file
        }
    }

    fun watchLibrary() {
        val watchChannel = Path.of(globalMangaPath.path).toFile().asWatchChannel()
        val taskQueue = TaskQueue()

        GlobalScope.launch() {
            taskQueue.runQueue()
            watchChannel.consumeEach { event ->
                log.debug("NEW EVENT: ${event.file.path} ${System.currentTimeMillis()}")

                if (event.file.name == ".DS_Store") return@consumeEach

                if (event.file.toPath().nameCount >= 2) {
                    val file = pathRecursion(event.file)


                    taskQueue.submit(
                        Task(
                            file = file,
                            state = TaskState.WAITING,
                            startTime = System.currentTimeMillis() + 10000L,
                        )
                    )
                }
            }
        }
    }
}

