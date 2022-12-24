package com.tankobon.manga.library.archive

import com.lordcodes.turtle.shellRun
import java.io.File

fun unRAR(file: File, increaseHierarchy: Boolean = false): File {
    val newFile = File("${file.parent}/${file.nameWithoutExtension}${if (increaseHierarchy) "/0" else ""}")
    newFile.mkdirs()
    shellRun("unrar", listOf("-inul", "-o+", "x", file.path, newFile.path))
    file.delete()

    return newFile;
}
