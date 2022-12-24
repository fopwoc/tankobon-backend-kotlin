package com.tankobon.manga.library.archive

import com.lordcodes.turtle.shellRun
import java.io.File

fun unZIP(file: File, increaseHierarchy: Boolean = false): File {
    val newFile = File("${file.parent}/${file.nameWithoutExtension}${if (increaseHierarchy) "/0" else ""}")
    newFile.mkdirs()
    shellRun("7z", listOf("-aoa", "x", file.path, "-o${newFile.path}"))
    file.delete()

    return newFile;
}
