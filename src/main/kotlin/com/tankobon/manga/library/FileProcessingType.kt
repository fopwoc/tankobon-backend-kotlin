package com.tankobon.manga.library

import com.tankobon.utils.archive.unRAR
import com.tankobon.utils.archive.unZIP
import com.tankobon.utils.imageConverter
import com.tankobon.utils.logger
import java.io.File

enum class FileProcessingType { ARCHIVE, IMAGES, ALL, UNSUPPORTED }

fun fileProcessing(
    file: File,
    type: FileProcessingType = FileProcessingType.ALL,
    increaseHierarchy: Boolean = false
): File? {
    val log = logger("file-processing")

    // skip macos .DS_Store file
    if (file.name.contains(".DS_Store")) {
        return null
    }

    when {

        // archives
        Regex("^(zip|cbz)\$").matches(file.extension) -> {
            if (type == FileProcessingType.ALL || type == FileProcessingType.ARCHIVE) {
                log.trace("unzip ${file.path}")
                return unZIP(file, increaseHierarchy)
            }
        }
        Regex("^(rar|cbr)\$").matches(file.extension) -> {
            if (type == FileProcessingType.ALL || type == FileProcessingType.ARCHIVE) {
                log.trace("unrar ${file.path}")
                return unRAR(file, increaseHierarchy)
            }
        }

        // images
        Regex("^(jpg)\$").matches(file.extension) -> {
            log.trace("jpg do nothing ${file.path}")
            //do nothing
        }
        Regex("^(png|PNG)\$").matches(file.extension) -> {
            if (type == FileProcessingType.ALL || type == FileProcessingType.IMAGES) {
                log.trace("png convert ${file.path}")
                imageConverter(file)
            }
        }
        Regex("^(jpeg|jpe|JPEG|JPE|JPG)\$").matches(file.extension) -> {
            if (type == FileProcessingType.ALL || type == FileProcessingType.IMAGES) {
                log.trace("jpg but with wrong extension ${file.path}")
                file.renameTo(File("${file.parentFile.path}/${file.nameWithoutExtension}.jpg"))
            }
        }

        //unsupported
        else -> {
            if (type == FileProcessingType.ALL || type == FileProcessingType.UNSUPPORTED) {
                log.debug("unsupported file ${file.path}")
                unsupportedExtension(file)
            }
        }
    }

    return  null;
}
