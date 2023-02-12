package com.tankobon.utils.archive

import com.lordcodes.turtle.shellRun
import com.tankobon.utils.formatFile
import com.tankobon.utils.logger
import java.io.File

fun unRAR(file: File, increaseHierarchy: Boolean = false): File {
    val log = logger("archive-rar")

    val newFile = formatFile(file, increaseHierarchy)
    newFile.mkdirs()

    log.trace("archive path ${file.path}")
    log.trace("new path ${newFile.path}")

    shellRun("unrar", listOf("-inul", "-o+", "x", file.path, newFile.path))
    file.delete()

    return newFile
}
