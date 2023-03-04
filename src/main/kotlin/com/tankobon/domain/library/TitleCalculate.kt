package com.tankobon.domain.library

import com.tankobon.domain.models.MangaUpdate
import com.tankobon.utils.isValidUUID
import com.tankobon.utils.logger
import java.io.File
import java.nio.file.Path
import java.util.UUID

fun titleCalculate(task: Task): MangaUpdate {
    val log = logger("library-title")
    var file = task.file
    log.trace("current work is ${file.name} ${file.path}")

    var originalTitleName: String? = null

    if (file.isFile && !file.name.contains(".DS_Store")) {
        log.debug("${file.name} is file")

        val archiveFile = fileRouter(
            file,
            type = FileRouterType.ARCHIVE,
            increaseHierarchy = true,
        )
        log.trace("archive folder is ${archiveFile?.name}")
        if (archiveFile != null) file = archiveFile.parentFile
    }

    if (file.isDirectory) {
        log.debug("${file.name} ${file.path} is dir")
        log.trace("${file.listFiles()?.map { it.name }}")

        if (!isValidUUID(file.name)) {
            originalTitleName = file.name

            val path = Path.of("${file.parentFile}/${task.id}").toFile()
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

    val fileList = file.listFiles()
        ?.filter { it.isDirectory } ?: emptyList()

    val result = (
        fileList.filter { isValidUUID(it.nameWithoutExtension) }
            .plus(fileList.filter { !isValidUUID(it.nameWithoutExtension) }.sorted())
        ).map { e ->
        var originalVolumeName: String? = null

        volumeCalculate(
            if (!isValidUUID(e.name)) {
                originalVolumeName = e.name
                val path = File(
                    "${e.parentFile.path}/" + UUID.randomUUID()
                )
                e.renameTo(path)
                path
            } else {
                e
            }
        ).copy(title = originalVolumeName)
    }

    log.debug("work for $file ends")

    return if (result.flatMap { it.content }.isNotEmpty()) {
        MangaUpdate(
            id = task.id,
            title = originalTitleName,
            volume = result,
        )
    } else {
        log.warn("title ${file.name} is empty")
        file.deleteRecursively()
        MangaUpdate(
            id = task.id,
            title = originalTitleName,
            volume = result,
        )
    }
}
