package com.tankobon.manga.filesystem

import java.io.File

fun fileLevelRecursion(file: File, origin: String) {
    file.listFiles()?.forEach { e ->
        if (e.isDirectory) {
            fileLevelRecursion(e, origin)
            e.delete()
        }
        if (e.isFile && !e.name.contains(".DS_Store")) {
            e.copyTo(File("$origin/${e.name}"), true)
            e.delete()
        }
    }
}
