package aspirin.tankobon.manga.filesystem

import aspirin.tankobon.logger
import java.io.File
import java.nio.file.Files

fun prepareTitle(titleDir: File): List<Int> {
    logger.info("Title preparation: ${titleDir.path}")

    titleDir.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
        ?.forEach { archiveNavigator(it) }

    titleDir.listFiles()?.filter { it.isDirectory }
        ?.sortedBy { it.name.toString() }
        ?.forEachIndexed { i, e ->
            prepareVolume(
                if (!Regex("^\\d*\$").matches(e.name))
                    Files.move(e.toPath(), File("${e.parentFile.path}/${i}").toPath()).toFile()
                else e,
            )
        }

    return titleDir.listFiles()
        ?.filter { it.isDirectory }
        ?.sortedBy { it.name.toString() }
        ?.map { e ->
            e.listFiles()
                ?.count { i -> i.isFile && !e.name.contains(".DS_Store") }
                ?.minus(1) ?: -1
        }?.toList() ?: listOf()

}