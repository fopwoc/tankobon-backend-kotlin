package com.tankobon.database.service

import com.tankobon.database.DatabaseInstance
import com.tankobon.database.model.Manga
import com.tankobon.database.model.MangaModel
import com.tankobon.database.model.MangaPayload
import com.tankobon.database.model.MangaUpdate
import com.tankobon.database.model.MangaUpdatePayload
import com.tankobon.database.model.toManga
import com.tankobon.globalThumbPath
import com.tankobon.utils.uuidFromString
import com.tankobon.webserver.InternalServerError
import io.ktor.server.plugins.NotFoundException
import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class MangaService {
    val database = DatabaseInstance.instance

    suspend fun getMangaList(payload: MangaPayload?): List<Manga> = newSuspendedTransaction(db = database) {
        val query = if (!payload?.search.isNullOrBlank()) {
            MangaModel.select { MangaModel.title match payload?.search.toString() }
        } else {
            MangaModel.selectAll()
        }

        return@newSuspendedTransaction query.limit(payload?.limit ?: 10, offset = payload?.offset ?: 0)
            .map { it.toManga() }
    }

    suspend fun getManga(id: String): Manga = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction MangaModel
            .select {
                MangaModel.id eq (uuidFromString(id) ?: throw NotFoundException())
            }.firstOrNull()?.toManga()
            ?: throw NotFoundException()
    }

    private suspend fun addMangaList(mangaUpdate: MangaUpdate) = newSuspendedTransaction(db = database) {
        MangaModel.insert {
            it[id] = uuidFromString(mangaUpdate.id) ?: throw InternalServerError()
            it[title] = mangaUpdate.title.orEmpty()
            it[description] = ""
            it[cover] = ""
            it[volume] = Json.encodeToString(mangaUpdate.volume)
        }
    }

    @Deprecated("TODO - rework from list to single")
    suspend fun updateMangaList(listMangaUpdate: List<MangaUpdate>) = newSuspendedTransaction(db = database) {
        val currentList = MangaModel.selectAll().map { it.toManga() }

        currentList.filter { e -> listMangaUpdate.none { it.id == e.id } }
            .forEach { v ->
                MangaModel.deleteWhere { MangaModel.id eq uuidFromString(v.id) }
                File("${globalThumbPath.path}/${v.id}").deleteRecursively()
            }

        listMangaUpdate.forEach { e ->
            if (MangaModel.select { MangaModel.id eq uuidFromString(e.id) }.none()) {
                addMangaList(e)
            } else {
                MangaModel.update({ MangaModel.id eq uuidFromString(e.id) }) {
                    it[volume] = Json.encodeToString(e.volume)
                }
            }
        }
    }

    suspend fun updateManga(id: String?, mangaUpdate: MangaUpdatePayload) = newSuspendedTransaction(db = database) {
        MangaModel.update({ MangaModel.id eq uuidFromString(id) }) {
            it[title] = mangaUpdate.title
            it[description] = mangaUpdate.description
            it[cover] = mangaUpdate.cover
        }
    }
}
