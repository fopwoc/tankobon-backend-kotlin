package com.tankobon.domain.database.services

import com.tankobon.api.models.Manga
import com.tankobon.api.models.MangaPayload
import com.tankobon.api.models.MangaUpdatePayload
import com.tankobon.domain.database.models.MangaLibraryModel
import com.tankobon.domain.database.models.toManga
import com.tankobon.domain.models.MangaUpdate
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.injectLogger
import com.tankobon.utils.uuidFromString
import io.ktor.server.plugins.NotFoundException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.io.File

private const val MANGA_LIST_LIMIT = 10

class MangaService {
    companion object {
        val log by injectLogger()
    }

    val database = DatabaseProvider.get()

    suspend fun getMangaList(payload: MangaPayload?): List<Manga> = newSuspendedTransaction(db = database) {
        val query = if (!payload?.search.isNullOrBlank()) {
            MangaLibraryModel.select { MangaLibraryModel.title match payload?.search.toString() }
        } else {
            MangaLibraryModel.selectAll()
        }

        return@newSuspendedTransaction query.limit(payload?.limit ?: MANGA_LIST_LIMIT, offset = payload?.offset ?: 0)
            .map { it.toManga() }
    }

    suspend fun getManga(id: String): Manga = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction MangaLibraryModel
            .select {
                MangaLibraryModel.id eq (uuidFromString(id) ?: throw NotFoundException())
            }.firstOrNull()?.toManga()
            ?: throw NotFoundException()
    }

    suspend fun updateMangaLibrary(update: MangaUpdate) = newSuspendedTransaction(db = database) {
        log.debug("addMangaList is $update")

        if (update.volume.flatMap { it.content }.isEmpty()) {
            MangaLibraryModel.deleteWhere { MangaLibraryModel.id eq update.id }
            File("${ConfigProvider.get().library.thumbFile.path}/${update.id}").deleteRecursively()
        } else {
            if (MangaLibraryModel.select { MangaLibraryModel.id eq update.id }.none()) {
                MangaLibraryModel.insert {
                    it[id] = update.id
                    if (!update.title.isNullOrBlank()) it[title] = update.title
                    it[description] = ""
                    it[cover] = ""
                    it[volume] = Json.encodeToString(update.volume)
                }
            } else {
                MangaLibraryModel.update({ MangaLibraryModel.id eq update.id }) {
                    it[volume] = Json.encodeToString(update.volume)
                }
            }
        }
    }

    suspend fun updateManga(id: String?, mangaUpdate: MangaUpdatePayload) = newSuspendedTransaction(db = database) {
        MangaLibraryModel.update({ MangaLibraryModel.id eq uuidFromString(id) }) {
            it[title] = mangaUpdate.title
            it[description] = mangaUpdate.description
            it[cover] = mangaUpdate.cover
        }
    }
}
