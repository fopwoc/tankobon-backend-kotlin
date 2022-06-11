package aspirin.tankobon.manga.filesystem

import aspirin.tankobon.database.model.MangaUpdate
import aspirin.tankobon.globalMangaPath
import aspirin.tankobon.logger
import aspirin.tankobon.utils.isValidUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.Path
import java.util.Collections
import java.util.UUID
import kotlin.system.measureTimeMillis

fun prepareLibrary(trigger: String? = null): List<MangaUpdate> {
    if (trigger?.isNotEmpty() == true) logger.info("Library prepare") else logger.info("Library prepare. Trigger: $trigger")

    val updateList: MutableList<MangaUpdate> = Collections.synchronizedList(mutableListOf<MangaUpdate>())

    val elapsed = measureTimeMillis {
        runBlocking {
            coroutineScope {
                globalMangaPath.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
                    ?.forEach {
                        println("prepareLibrary archiveNavigator ${Thread.currentThread().name}")
                        withContext(Dispatchers.Default) { fileNavigator(it) }
                    }
            }

            globalMangaPath.listFiles()?.filter { it.isDirectory }
                ?.forEach { e ->
                    if (isValidUUID(e.name)) {
                        println("updateList add  $e.name ${Thread.currentThread().name}")
                        updateList.add(
                            MangaUpdate(e.name, null, prepareTitle(e))
                        )
                    } else {
                        val uuid = UUID.randomUUID()
                        val path = Path.of("${e.parentFile}/$uuid").toFile()
                        e.renameTo(path)
                        println("updateList add $uuid.toString() ${Thread.currentThread().name}")
                        updateList.add(
                            MangaUpdate(
                                uuid.toString(),
                                e.name,
                                prepareTitle(path),
                            )
                        )
                    }
                }
        }
    }

    logger.info("Library preparation successfully completed. Time elapsed: ${elapsed / 1000} seconds")
    return updateList
}
