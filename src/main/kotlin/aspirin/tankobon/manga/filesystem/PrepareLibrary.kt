package aspirin.tankobon.manga.filesystem

import aspirin.tankobon.database.model.MangaUpdate
import aspirin.tankobon.globalMangaPath
import aspirin.tankobon.logger
import aspirin.tankobon.utils.isValidUUID
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

fun prepareLibrary(trigger: String?): List<MangaUpdate> {
    logger.info("Library prepare. Trigger: $trigger")
    return prepareLibrary()
}

fun prepareLibrary(): List<MangaUpdate> {
    globalMangaPath.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
        ?.forEach { archiveNavigator(it) }

    val updateList: MutableList<MangaUpdate> = emptyList<MangaUpdate>().toMutableList()

    globalMangaPath.listFiles()?.filter { it.isDirectory }
        ?.forEach { e ->
            if (isValidUUID(e.name)) {
                updateList.add(
                    MangaUpdate(e.name, null, prepareTitle(e))
                )
            } else {
                val uuid = UUID.randomUUID()
                val path = Files.move(e.toPath(), Path.of("${e.parentFile}/$uuid")).toFile()
                updateList.add(
                    MangaUpdate(
                        uuid.toString(),
                        e.name,
                        prepareTitle(path),
                    )
                )
            }
        }

    logger.info("Library preparation successfully completed")
    return updateList
}