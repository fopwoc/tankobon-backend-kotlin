package com.tankobon.domain.database.services

import com.tankobon.api.models.Manga
import com.tankobon.api.models.MangaFilterPayloadModel
import com.tankobon.api.models.MangaTitle
import com.tankobon.api.models.MangaTitleUpdatePayloadModel
import com.tankobon.api.models.MangaVolumeUpdatePayloadModel
import com.tankobon.domain.database.models.MangaPageTable
import com.tankobon.domain.database.models.MangaTitleTable
import com.tankobon.domain.database.models.MangaVolumeTable
import com.tankobon.domain.database.models.toManga
import com.tankobon.domain.database.models.toMangaPage
import com.tankobon.domain.database.models.toMangaTitle
import com.tankobon.domain.database.models.toMangaVolume
import com.tankobon.domain.database.services.manga.createMangePage
import com.tankobon.domain.database.services.manga.createMangeTitle
import com.tankobon.domain.database.services.manga.createMangeVolume
import com.tankobon.domain.database.services.manga.deleteMangaPage
import com.tankobon.domain.database.services.manga.deleteMangaTitle
import com.tankobon.domain.database.services.manga.deleteMangaVolume
import com.tankobon.domain.database.services.manga.doesTitleExists
import com.tankobon.domain.database.services.manga.updateDateMangaTitle
import com.tankobon.domain.models.IdEntity
import com.tankobon.domain.models.MangaUpdate
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.injectLogger
import com.tankobon.utils.uuidFromString
import io.ktor.server.plugins.NotFoundException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

private const val MANGA_LIST_LIMIT = 10

class MangaService {
    companion object {
        val log by injectLogger()
    }

    val database = DatabaseProvider.get()

    suspend fun getMangaList(
        payload: MangaFilterPayloadModel?,
    ): List<MangaTitle> = newSuspendedTransaction(db = database) {
        val query = if (!payload?.search.isNullOrBlank()) {
            MangaTitleTable.select { MangaTitleTable.title match payload?.search.toString() }
        } else {
            MangaTitleTable.selectAll()
        }

        return@newSuspendedTransaction query.limit(
            payload?.limit ?: MANGA_LIST_LIMIT,
            offset = payload?.offset ?: 0
        ).map { it.toMangaTitle() }
    }

    suspend fun getManga(
        id: UUID?,
    ): Manga = newSuspendedTransaction(db = database) {
        val mangaTitle = MangaTitleTable
            .select {
                MangaTitleTable.id eq id
            }.firstOrNull()?.toManga()
            ?: throw NotFoundException()

        val mangaVolume = MangaVolumeTable.select {
            MangaVolumeTable.titleId eq mangaTitle.id
        }.sortedBy { it[MangaVolumeTable.order] }.map { volume ->
            volume.toMangaVolume().copy(
                content = MangaPageTable.select {
                    MangaPageTable.volumeId eq volume[MangaVolumeTable.id]
                }.sortedBy { it[MangaPageTable.order] }.map { it.toMangaPage() }
            )
        }

        return@newSuspendedTransaction mangaTitle.copy(content = mangaVolume)
    }

    suspend fun updateManga(
        update: MangaUpdate,
    ) = newSuspendedTransaction(db = database) {
        log.debug("addMangaList is $update")

        val empty = update.content.flatMap { it.content }.isEmpty()

        if (!empty) {
            val condition = doesTitleExists(update.id)
            log.debug("${update.id} is $condition")

            if (condition) {
                updateMangaContent(update)
            } else {
                createMangeTitle(update)
                createMangeVolume(update.id, update.content)
                update.content.map { createMangePage(it.id, it.content) }
            }
        } else {
            deleteMangaPage(update.id)
            deleteMangaVolume(update.id)
            deleteMangaTitle(update.id)
        }
    }

    suspend fun updateMangaInfo(
        titleId: String?,
        mangaUpdate: MangaTitleUpdatePayloadModel,
    ) = newSuspendedTransaction(db = database) {
        MangaTitleTable.update({ MangaTitleTable.id eq uuidFromString(titleId) }) {
            it[this.title] = mangaUpdate.title
            it[this.description] = mangaUpdate.description
            it[this.modified] = System.currentTimeMillis() // TODO all time to Instant.now().toEpochMilli()
        }
    }

    suspend fun updateMangaVolumeInfo(
        titleId: String?,
        volumeId: String?,
        mangaUpdate: MangaVolumeUpdatePayloadModel,
    ) = newSuspendedTransaction(db = database) {
        // TODO: check is title id correct before changing volume title

        MangaVolumeTable.update({ MangaVolumeTable.id eq uuidFromString(volumeId) }) {
            it[this.title] = mangaUpdate.title
        }
        updateDateMangaTitle(UUID.fromString(titleId))
    }

    suspend fun cleanupMangaByIds(
        ids: List<UUID>,
    ) = newSuspendedTransaction(db = database) {
        val mangaList = MangaTitleTable.selectAll().map { it[MangaTitleTable.id].value }
        val deletedIds = mangaList.filter { !ids.contains(it) }

        log.debug("deleted ids $deletedIds")

        deletedIds.forEach {
            updateManga(MangaUpdate(it))
        }
    }

    private suspend fun updateMangaContent(
        update: MangaUpdate,
    ) = newSuspendedTransaction(db = database) {
        val originalManga = getManga(update.id)

        val updateCorrectOrder = sortEntity(
            MangaVolumeTable.select {
                MangaVolumeTable.titleId eq originalManga.id
            }.orderBy(MangaVolumeTable.order).map { it.toMangaVolume() },
            update.content,
        ).map { volume ->
            volume.copy(
                content = sortEntity(
                    MangaPageTable.select {
                        MangaPageTable.id eq volume.id
                    }.orderBy(MangaPageTable.order).map { it.toMangaPage() },
                    volume.content,
                )
            )
        }

        val updateCorrectTitle = updateCorrectOrder.mapIndexed() { i, volume ->
            if (volume.title != null) return@mapIndexed volume

            val originalVolume = originalManga.content.elementAtOrNull(i)

            if (originalVolume != null) {
                volume.copy(title = originalVolume.title)
            } else {
                volume
            }
        }

        val isContentUpdated = originalManga.content.map { it.copy(title = null) } != updateCorrectOrder

        log.debug("has content updated? $isContentUpdated")

        if (isContentUpdated) {
            newSuspendedTransaction(db = database) {
                originalManga.content.forEach { volume -> MangaPageTable.deleteWhere { volumeId eq volume.id } }
                MangaVolumeTable.deleteWhere { titleId eq originalManga.id }
                commit()

                createMangeVolume(originalManga.id, updateCorrectTitle)
                updateCorrectTitle.forEach { createMangePage(it.id, it.content) }

                updateDateMangaTitle(originalManga.id)
            }
        }
    }
}

fun <T : IdEntity<UUID>> sortEntity(originalList: List<T>, updateList: List<T>): List<T> {
    val orderById = originalList.withIndex().associate { (index, it) -> it.id to index }
    return updateList.sortedBy { orderById[it.id] ?: Integer.MAX_VALUE }
}
