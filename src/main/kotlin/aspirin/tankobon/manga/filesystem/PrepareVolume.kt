package aspirin.tankobon.manga.filesystem

import aspirin.tankobon.globalThumbPath
import aspirin.tankobon.logger
import aspirin.tankobon.utils.thumbnailGenerator
import java.io.File
import java.nio.file.Files

fun prepareVolume(volumePath: File) {
    logger.info("Volume preparation: ${volumePath.path}")

    volumePath.listFiles()?.filter { e -> e.isDirectory }
        ?.forEach {
            fileLevelRecursion(it, volumePath.absolutePath)
            it.delete()
        }

    val newThumb = File("${globalThumbPath.path}/${volumePath.parentFile.name}/${volumePath.name}")
    newThumb.mkdirs()

    volumePath.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
        ?.forEach { imageNavigator(it) }


    if (newThumb.listFiles().isNullOrEmpty()) {
        volumePath.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
            ?.sortedBy { it.name.toString() }
            ?.forEachIndexed { i, e ->
                val newFile = if (!Regex("^\\d*\$").matches(e.nameWithoutExtension)) {
                    Files.move(e.toPath(), File("${e.parentFile.path}/${i}.${e.extension}").toPath()).toFile()
                } else e
                thumbnailGenerator(newFile, newThumb)
            }
    }
}
