package com.tankobon.domain.library

import com.tankobon.api.models.MangaPageModel
import com.tankobon.api.models.MangaVolumeModel
import com.tankobon.domain.database.services.MangaService
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.utils.isValidUUID
import com.tankobon.utils.logger
import com.tankobon.utils.md5
import com.tankobon.utils.thumbnailGenerator
import com.tankobon.utils.uuidFromString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.UUID

fun volumeCalculate(file: File): MangaVolumeModel {
    val log = logger("library-volume")
    log.trace("current work is ${file.name} ${file.path}")

    val idOfVolume = uuidFromString(file.name)

    if (idOfVolume == null) {
        MangaService.log.debug("uuid ${file.name} is actually not uuid")
        throw Exception()
    }

    runBlocking {
        file.listFiles()
            ?.filter { it.isFile && !it.name.equals(".DS_Store") }
            ?.forEach { e ->
                launch(Dispatchers.Default) {
                    fileRouter(e, type = FileRouterType.IMAGES)
                }
            }
    }

    if (file.listFiles()?.none { !it.name.equals(".DS_Store") } == true) {
        log.warn("volume ${file.name} is empty")
        file.delete()
        return MangaVolumeModel(
            id = idOfVolume,
            title = null,
            content = emptyList(),
        )
    }

    val files = file.listFiles()
        ?.filter { it.isFile && !it.name.equals(".DS_Store") } ?: emptyList()

    // TODO: works good, but looks awful
    val finalListPages = (
        files.filter { isValidUUID(it.nameWithoutExtension) }
            .plus(files.filter { !isValidUUID(it.nameWithoutExtension) }.sorted())
        ).associate { pageFile ->
        if (!isValidUUID(pageFile.nameWithoutExtension)) {
            val id = UUID.randomUUID()

            val path = File(
                "${pageFile.parentFile.path}/" + "$id.${pageFile.extension}"
            )
            log.trace("rename ${pageFile.name} to ${path.path}")
            pageFile.renameTo(path)
            id to path
        } else {
            log.trace("${pageFile.name} is already UUID")
            UUID.fromString(pageFile.nameWithoutExtension) to pageFile
        }
    }

    // TODO: thumbnail rework to be more efficient
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

    return MangaVolumeModel(
        id = idOfVolume,
        title = null,
        content = finalListPages.map {
            MangaPageModel(
                id = it.key,
                hash = md5(it.value),
            )
        }
    )
}
