package com.tankobon.manga.library.filesystem

import com.tankobon.database.model.MangaVolume
import com.tankobon.globalThumbPath
import com.tankobon.manga.library.FileRouterType
import com.tankobon.manga.library.fileRouter
import com.tankobon.utils.logger
import com.tankobon.utils.md5
import com.tankobon.utils.thumbnailGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

fun volume(file: File): MangaVolume {
    val log = logger("fs-volume")
    log.trace("current work is ${file.name} ${file.path}")

    runBlocking {
        file.listFiles()
            ?.filter { it.isFile && !it.name.equals(".DS_Store") }
            ?.forEach { e ->
                launch(Dispatchers.Default) {
                    fileRouter(e, type = FileRouterType.IMAGES)
                }
            }
    }

    file.listFiles()
        ?.filter { it.isFile && !it.name.equals(".DS_Store") }
        ?.sorted()
        ?.forEachIndexed { i, e ->
            // TODO formatting of %0Nd to utils
            val path = File("${e.parentFile.path}/${"%05d".format(i)}.${e.extension}")
            log.trace("rename ${e.name} to ${path.path}")
            e.renameTo(path)
        }

    if (file.listFiles()?.none { !it.name.equals(".DS_Store") } == true) {
        log.warn("volume ${file.name} is empty")
        file.delete()
        return MangaVolume(
            order = 0,
            title = null,
            content = emptyList(),
        )
    }

    val newThumb = File("${globalThumbPath.path}/${file.parentFile.name}/${file.name}")
    newThumb.mkdirs()

    runBlocking {
        file.listFiles()
            ?.filter { it.isFile && !it.name.equals(".DS_Store") }
            ?.forEach {
                launch(Dispatchers.Default) {
                    thumbnailGenerator(it, newThumb)
                }
            }
    }

    return MangaVolume(
        order = 0,
        title = null,
        content = file.listFiles()
            ?.filter { it.isFile && !it.name.equals(".DS_Store") }
            ?.sorted()
            ?.map { md5(it) } ?: listOf()
    )
}
