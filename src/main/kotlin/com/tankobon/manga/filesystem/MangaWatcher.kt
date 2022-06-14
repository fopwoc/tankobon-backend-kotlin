package com.tankobon.manga.filesystem

import com.tankobon.database.service.MangaService
import com.tankobon.globalMangaPath
import com.tankobon.globalThumbPath
import com.tankobon.logger
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds

@DelicateCoroutinesApi
class MangaWatcher(private val mangaService: MangaService) {
    fun watchFolder() {
        globalMangaPath.mkdirs()
        globalThumbPath.mkdirs()

        runBlocking {
            logger.info("Library preparation")
            mangaService.updateMangaList(
                prepareLibrary()
            )

            GlobalScope.launch {
                try {
                    logger.info("Watching directory for changes")
                    val watchKey = withContext(Dispatchers.IO) {
                        Path.of(globalMangaPath.path)
                            .register(
                                FileSystems.getDefault().newWatchService(),
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_MODIFY,
                                StandardWatchEventKinds.ENTRY_DELETE,
                            )
                    }

                    while (true) {
                        for (event in watchKey.pollEvents()) {
                            mangaService.updateMangaList(
                                prepareLibrary(event.kind().name())
                            )
                        }

                        delay(10000L)

                        val valid = watchKey.reset()
                        if (!valid) {
                            break
                        }
                    }
                } catch (e: Exception) {
                    logger.error(e.stackTraceToString())
                }
            }
        }
    }
}
