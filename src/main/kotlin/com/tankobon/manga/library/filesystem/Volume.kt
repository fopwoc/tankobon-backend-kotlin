package com.tankobon.manga.library.filesystem

import com.tankobon.manga.filesystem.fileLevelRecursion
import com.tankobon.manga.library.ProcessingType
import com.tankobon.manga.library.fileProcessing
import com.tankobon.utils.logger
import java.io.File

fun volume(file: File) {
    val log = logger("fs-volume")

    if (file.isFile) {
        log.debug("${file.name} is file")
        val newDir = fileProcessing(file, type = ProcessingType.ARCHIVE)
        if (newDir != null) fileLevelRecursion(newDir, newDir.parentFile.absolutePath)
    } else {
        fileLevelRecursion(file, file.parentFile.absolutePath)
    }

//    val newThumb = File("${globalThumbPath.path}/${file.name}")
//    newThumb.mkdirs()
//
//    if (newThumb.listFiles().isNullOrEmpty()) {
//
//    }

    file.listFiles()?.map { log.trace("NAMES ${it.name}") }

//    file.listFiles()?.filter { it.isFile && !it.name.equals(".DS_Store") }?.sortedBy { it.name.toString() }?.forEachIndexed { i, e ->
//        val path = File("${e.parentFile.path}/$i.${e.extension}")
//        log.debug("file rename path is ${path.absolutePath}")
//        e.renameTo(path)
//        //thumbnailGenerator(path, newThumb)
//    }
}
