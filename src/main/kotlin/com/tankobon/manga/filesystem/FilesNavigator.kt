package com.tankobon.manga.filesystem

import com.tankobon.manga.archive.unRAR
import com.tankobon.manga.archive.unZIP
import com.tankobon.utils.imageConverter
import java.io.File

fun fileNavigator(file: File) {
    when {
        // archives
        Regex("^(zip|cbz)\$").matches(file.extension) -> unZIP(file)
        Regex("^(rar|cbr)\$").matches(file.extension) -> unRAR(file)
        // images
        Regex("^(jpg)\$").matches(file.extension) -> {}
        Regex("^(png|PNG)\$").matches(file.extension) ->
            imageConverter(file)
        Regex("^(jpeg|jpe|JPEG|JPE|JPG)\$").matches(file.extension) ->
            file.renameTo(File("${file.parentFile.path}/${file.nameWithoutExtension}.jpg"))
        else -> unsupportedExtension(file)
    }
}
