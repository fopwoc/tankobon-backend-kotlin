package com.tankobon.manga.library.filesystem

import com.tankobon.manga.library.FileRouterType
import com.tankobon.manga.library.Task
import com.tankobon.manga.library.fileRouter
import com.tankobon.utils.isValidUUID
import com.tankobon.utils.logger
import java.io.File
import java.nio.file.Path

fun title(task: Task): List<List<String>> {
    val log = logger("fs-title")

    var file = task.file
    log.trace("current work is ${file.name} ${file.path}")

    if (file.isFile && !file.name.contains(".DS_Store")) {
        log.debug("${file.name} is file")

        val archiveFile = fileRouter(file, type = FileRouterType.ARCHIVE, increaseHierarchy = true)
        log.trace("archive folder is ${archiveFile?.name}")
        if (archiveFile != null) file = archiveFile.parentFile
    }

    if (file.isDirectory) {
        log.debug("${file.name} ${file.path} is dir")
        log.trace("${file.listFiles()?.map { it.name }}")

        if (!isValidUUID(file.name)) {
            val path = Path.of("${file.parentFile}/${task.uuid}").toFile()
            file.renameTo(path)
            file = path
        }

        file.listFiles()?.forEach { e ->
            if (e.isFile && !e.name.contains(".DS_Store")) {
                log.debug("${e.name} is file")

                val newDir = fileRouter(e, type = FileRouterType.ARCHIVE)
                if (newDir != null) {
                    newDir.listFiles()?.filter { it.isDirectory }?.forEach {
                        fileLevelRecursion(it, newDir.absolutePath)
                    }
                }
            }

            if (e.isDirectory) {
                log.debug("${e.name} is directory")

                e.listFiles()?.forEach {
                    fileLevelRecursion(it, e.absolutePath)
                }
            }
        }
    }

    val result = file.listFiles()
        ?.filter { it.isDirectory }
        ?.sorted()
        ?.mapIndexed { i, e ->
            volume(
                if (!Regex("^\\d{4}\$").matches(e.name)) {
                    val path = File("${e.parentFile.path}/${"%04d".format(i)}")
                    e.renameTo(path)
                    path
                } else {
                    e
                }
            )
        } ?: listOf()

    log.debug("work for $file ends")
    return if (result.flatten().isNotEmpty()) {
        result
    } else {
        log.warn("title ${file.name} is empty")
        file.delete()
        emptyList()
    }
}
