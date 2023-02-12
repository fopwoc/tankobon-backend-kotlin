package com.tankobon.domain.library

import com.tankobon.api.models.MangaVolume
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.utils.formatDigits
import com.tankobon.utils.logger
import com.tankobon.utils.md5
import com.tankobon.utils.thumbnailGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

fun volumeCalculate(file: File): MangaVolume {
    val log = logger("library-volume")
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
            val path = File(
                "${e.parentFile.path}/" +
                    "${formatDigits(i, ConfigProvider.get().library.volumeDigits)}.${e.extension}"
            )
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

    val newThumb = File("${ConfigProvider.get().library.thumbFile.path}/${file.parentFile.name}/${file.name}")
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
