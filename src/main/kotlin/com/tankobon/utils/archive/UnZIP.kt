package com.tankobon.utils.archive

import com.lordcodes.turtle.shellRun
import com.tankobon.utils.formatFile
import com.tankobon.utils.logger
import java.io.File

fun unZIP(file: File, increaseHierarchy: Boolean = false): File {
    val log = logger("archive-zip")

    val newFile = formatFile(file, increaseHierarchy)
    newFile.mkdirs()

    log.trace("archive path ${file.path}")
    log.trace("new path ${newFile.path}")

    shellRun("7z", listOf("-aoa", "x", file.path, "-o${newFile.path}"))
    file.delete()

    return newFile
}
