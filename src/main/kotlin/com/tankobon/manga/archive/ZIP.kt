package com.tankobon.manga.archive

import net.lingala.zip4j.ZipFile
import java.io.File

fun unZIP(path: File) {
    val newPath = File("${path.parent}/${path.nameWithoutExtension}/0")
    newPath.mkdirs()
    ZipFile(path.path).extractAll(newPath.path)
    path.delete()
}
