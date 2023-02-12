package com.tankobon.utils

import com.tankobon.domain.providers.ConfigProvider
import java.io.File

fun unsupported(file: File) {
    val log = logger("fs-unsupported")
    val unsupportedFile = ConfigProvider.get().library.unsupportedFile

    log.trace("found unsupported ${file.name}. full path: ${file.absolutePath}")

    unsupportedFile.mkdirs()
    file.renameTo(
        File("${unsupportedFile.path}/${formatTime()}_${file.name}")
    )
}
