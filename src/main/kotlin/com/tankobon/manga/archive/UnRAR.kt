package com.tankobon.manga.archive

import com.lordcodes.turtle.shellRun
import java.io.File

fun unRAR(path: File) {
    val newPath = File("${path.parent}/${path.nameWithoutExtension}/0")
    newPath.mkdirs()
    shellRun("unrar", listOf("x", path.path, newPath.path))
    path.delete()
}
