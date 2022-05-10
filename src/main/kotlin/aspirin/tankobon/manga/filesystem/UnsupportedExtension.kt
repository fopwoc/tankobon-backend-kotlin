package aspirin.tankobon.manga.filesystem

import aspirin.tankobon.logger
import java.io.File
import java.nio.file.Files

private val unsupportedPath = File("unsupported")

fun unsupportedExtension(file: File) {
    logger.warn("Extension ${file.extension} is not supported. full path: ${file.absolutePath}")
    unsupportedPath.mkdirs()
    Files.move(file.toPath(), File("${unsupportedPath.path}/${file.name}").toPath())
}