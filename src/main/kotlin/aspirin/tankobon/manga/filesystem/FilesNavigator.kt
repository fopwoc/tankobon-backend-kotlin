package aspirin.tankobon.manga.filesystem

import aspirin.tankobon.manga.archive.unRAR
import aspirin.tankobon.manga.archive.unZIP
import aspirin.tankobon.utils.imageConverter
import java.io.File
import java.nio.file.Files

fun archiveNavigator(file: File) {
    when {
        Regex("^(zip|cbz)\$").matches(file.extension) -> unZIP(file)
        Regex("^(rar|cbr)\$").matches(file.extension) -> unRAR(file)
        else -> unsupportedExtension(file)
    }
}

fun imageNavigator(file: File) {
    when {
        Regex("^(jpg)\$").matches(file.extension) -> {}
        Regex("^(png|PNG)\$").matches(file.extension) ->
            imageConverter(file)
        Regex("^(jpeg|jpe|JPEG|JPE|JPG)\$").matches(file.extension) ->
            Files.move(file.toPath(), File("${file.parentFile.path}/${file.nameWithoutExtension}.jpg").toPath())
        else -> unsupportedExtension(file)
    }
}