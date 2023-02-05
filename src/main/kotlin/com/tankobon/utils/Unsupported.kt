package com.tankobon.utils

import com.tankobon.globalUnsupportedPath
import java.io.File

fun unsupported(file: File) {
    val log = logger("fs-unsupported")

    log.trace("found unsupported ${file.name}. full path: ${file.absolutePath}")

    globalUnsupportedPath.mkdirs()
    file.renameTo(
        File("${globalUnsupportedPath.path}/${formatTime()}_${file.name}")
    )
}
