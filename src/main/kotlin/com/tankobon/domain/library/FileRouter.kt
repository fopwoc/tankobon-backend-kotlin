package com.tankobon.domain.library

import com.tankobon.utils.archive.unRAR
import com.tankobon.utils.archive.unZIP
import com.tankobon.utils.imageConverter
import com.tankobon.utils.isJpgFile
import com.tankobon.utils.isPngFile
import com.tankobon.utils.isRarFile
import com.tankobon.utils.isWrongJpgFile
import com.tankobon.utils.isZipFile
import com.tankobon.utils.logger
import com.tankobon.utils.unsupported
import java.io.File

enum class FileRouterType { ARCHIVE, IMAGES, ALL }

fun fileRouter(
    file: File,
    type: FileRouterType = FileRouterType.ALL,
    increaseHierarchy: Boolean = false,
): File? {
    val log = logger("file-router")

    // skip macos .DS_Store file
    if (file.name.contains(".DS_Store")) {
        return null
    }

    when {
        // archives
        isZipFile(file.extension) -> {
            if (type == FileRouterType.ALL || type == FileRouterType.ARCHIVE) {
                log.trace("unzip ${file.path}")
                return unZIP(file, increaseHierarchy)
            }
        }

        isRarFile(file.extension) -> {
            if (type == FileRouterType.ALL || type == FileRouterType.ARCHIVE) {
                log.trace("unrar ${file.path}")
                return unRAR(file, increaseHierarchy)
            }
        }

        // images
        isPngFile(file.extension) -> {
            if (type == FileRouterType.ALL || type == FileRouterType.IMAGES) {
                log.trace("png convert ${file.path}")
                imageConverter(file)
            }
        }

        isWrongJpgFile(file.extension) -> {
            if (type == FileRouterType.ALL || type == FileRouterType.IMAGES) {
                log.trace("jpg but with wrong extension ${file.path}")
                file.renameTo(File("${file.parentFile.path}/${file.nameWithoutExtension}.jpg"))
            }
        }

        isJpgFile(file.extension) -> {
            log.trace("jpg do nothing ${file.path}")
            // do nothing
        }

        // unsupported
        else -> {
            log.debug("unsupported file ${file.path}")
            unsupported(file)
        }
    }

    return null
}
