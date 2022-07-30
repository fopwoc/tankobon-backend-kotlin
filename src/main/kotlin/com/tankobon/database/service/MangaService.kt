package com.tankobon.database.service

import com.tankobon.database.DatabaseInstance
import com.tankobon.database.model.Manga
import com.tankobon.database.model.MangaModel
import com.tankobon.database.model.MangaPayload
import com.tankobon.database.model.MangaUpdate
import com.tankobon.database.model.MangaUpdatePayload
import com.tankobon.database.model.toManga
import com.tankobon.globalThumbPath
import com.tankobon.utils.getUUID
import com.tankobon.webserver.ContentNotFoundException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.io.File
import java.util.*

class MangaService {
    val database = DatabaseInstance.instance

    suspend fun getMangaList(payload: MangaPayload?): List<Manga> = newSuspendedTransaction(db = database) {

        val query = if (!payload?.search.isNullOrBlank()) {
            MangaModel.select { MangaModel.title like payload!!.search!! }
        } else {
            MangaModel.selectAll()
        }

        return@newSuspendedTransaction query.limit(payload?.limit ?: 10, offset = payload?.offset ?: 0)
            .map { it.toManga() }
    }

    suspend fun getManga(id: String): Manga = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction MangaModel
            .select{MangaModel.id eq (getUUID(id) ?: throw ContentNotFoundException())}.firstOrNull()?.toManga()
            ?: throw ContentNotFoundException()
    }

    private suspend fun addMangaList(mangaUpdate: MangaUpdate) = newSuspendedTransaction(db = database) {
        MangaModel.insert {
            it[id] = UUID.fromString(mangaUpdate.id)
            it[title] = mangaUpdate.title ?: ""
            it[description] = ""
            it[cover] = ""
            it[volume] = Json.encodeToString(mangaUpdate.volume)
        }
    }

    suspend fun updateMangaList(listMangaUpdate: List<MangaUpdate>) = newSuspendedTransaction(db = database) {
        val currentList = MangaModel.selectAll().map { it.toManga() }

        currentList.filter { e -> listMangaUpdate.none { it.id == e.id } }
            .forEach {
                MangaModel.deleteWhere { MangaModel.id eq UUID.fromString(it.id) }
                File("${globalThumbPath.path}/${it.id}").deleteRecursively()
            }

        listMangaUpdate.forEach { e ->
            if (MangaModel.select { MangaModel.id eq UUID.fromString(e.id) }.firstOrNull() == null) {
                addMangaList(e)
            } else {
                MangaModel.update({ MangaModel.id eq UUID.fromString(e.id) }) {
                    it[volume] = Json.encodeToString(e.volume)
                }
            }
        }
    }

    suspend fun updateManga(id: String?, mangaUpdate: MangaUpdatePayload) = newSuspendedTransaction(db = database) {
        MangaModel.update({ MangaModel.id eq UUID.fromString(id) }) {
            it[title] = mangaUpdate.title
            it[description] = mangaUpdate.description
            it[cover] = mangaUpdate.cover
        }
    }
}
