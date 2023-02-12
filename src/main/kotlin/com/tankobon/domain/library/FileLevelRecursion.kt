package com.tankobon.domain.library

import com.tankobon.utils.logger
import java.io.File

fun fileLevelRecursion(file: File, origin: String, isEnterLevel: Boolean = true) {
    val log = logger("file-recursion")

    if (isEnterLevel) {
        log.trace("file is ${file.name} ${file.path}")
        log.trace("origin is $origin")
    }

    file.listFiles()?.forEach { e ->
        if (e.isDirectory) {
            fileLevelRecursion(e, origin, false)
            e.delete()
        }
        if (e.isFile && !e.name.contains(".DS_Store")) {
            e.copyTo(File("$origin/${e.name}"), true)
            e.delete()
        }
    }
    if (file.isDirectory) file.delete()
}
