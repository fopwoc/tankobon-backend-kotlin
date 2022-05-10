package aspirin.tankobon.manga.filesystem

import aspirin.tankobon.database.service.MangaService
import aspirin.tankobon.globalMangaPath
import aspirin.tankobon.globalThumbPath
import aspirin.tankobon.logger
import kotlinx.coroutines.*
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds

@DelicateCoroutinesApi
class MangaWatcher(private val mangaService: MangaService) {
    fun watchFolder() {
        globalMangaPath.mkdirs()
        globalThumbPath.mkdirs()

        GlobalScope.launch {
            logger.info("Library preparation")
            withContext(Dispatchers.Default) {
                mangaService.updateMangaList(
                    prepareLibrary()
                )
            }

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
                        withContext(Dispatchers.Default) {
                            mangaService.updateMangaList(
                                prepareLibrary(event.kind().name())
                            )
                        }
                    }

                    withContext(Dispatchers.IO) {
                        Thread.sleep(10000)
                    }

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
