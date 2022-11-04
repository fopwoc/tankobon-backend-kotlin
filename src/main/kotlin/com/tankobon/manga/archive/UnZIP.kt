package com.tankobon.manga.archive

import com.lordcodes.turtle.shellRun
import java.io.File

fun unZIP(path: File) {
    val newPath = File("${path.parent}/${path.nameWithoutExtension}/0")
    newPath.mkdirs()
    shellRun("unzip", listOf(path.path, "-d", newPath.path))
    path.delete()
}
