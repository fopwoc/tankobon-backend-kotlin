package com.tankobon.manga.library

import com.tankobon.manga.filesystem.unsupportedExtension
import com.tankobon.manga.library.archive.unRAR
import com.tankobon.manga.library.archive.unZIP
import com.tankobon.utils.imageConverter
import java.io.File

enum class ProcessingType { ARCHIVE, IMAGES, ALL, UNSUPPORTED }

fun fileProcessing(file: File, type: ProcessingType = ProcessingType.ALL, increaseHierarchy: Boolean = false): File? {

    // skip macos .DS_Store file
    if (file.name == ".DS_Store") return null;

    when {

        // archives
        Regex("^(zip|cbz)\$").matches(file.extension) -> {
            if (type == ProcessingType.ALL || type == ProcessingType.ARCHIVE) {
                return unZIP(file, increaseHierarchy)
            }
        }
        Regex("^(rar|cbr)\$").matches(file.extension) -> {
            if (type == ProcessingType.ALL || type == ProcessingType.ARCHIVE) {
                return unRAR(file, increaseHierarchy)
            }
        }

        // images
        Regex("^(jpg)\$").matches(file.extension) -> {
            //do nothing
        }
        Regex("^(png|PNG)\$").matches(file.extension) -> {
            if (type == ProcessingType.ALL || type == ProcessingType.IMAGES) {
                imageConverter(file)
            }
        }
        Regex("^(jpeg|jpe|JPEG|JPE|JPG)\$").matches(file.extension) -> {
            if (type == ProcessingType.ALL || type == ProcessingType.IMAGES) {
                file.renameTo(File("${file.parentFile.path}/${file.nameWithoutExtension}.jpg"))
            }
        }

        //unsupported
        else -> {
            if (type == ProcessingType.ALL || type == ProcessingType.UNSUPPORTED) {
                unsupportedExtension(file)
            }
        }
    }

    return  null;
}
