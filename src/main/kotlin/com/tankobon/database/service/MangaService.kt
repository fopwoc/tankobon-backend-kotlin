package com.tankobon.database.service

import com.tankobon.database.model.Manga
import com.tankobon.database.model.MangaModel
import com.tankobon.database.model.MangaUpdate
import com.tankobon.database.model.toManga
import com.tankobon.globalThumbPath
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.io.File
import java.util.UUID

class MangaService(val database: Database) {

    suspend fun getMangaList(): List<Manga> = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction MangaModel.selectAll().map { it.toManga() }
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
            if (e.title?.isNotEmpty() == true) {
                addMangaList(e)
            } else {
                MangaModel.update({ MangaModel.id eq UUID.fromString(e.id) }) {
                    it[volume] = Json.encodeToString(e.volume)
                }
            }
        }
    }

    suspend fun updateManga(mangaUpdate: Manga) = newSuspendedTransaction(db = database) {
        MangaModel.update({ MangaModel.id eq UUID.fromString(mangaUpdate.id) }) {
            it[title] = mangaUpdate.title
            it[description] = mangaUpdate.description
            it[cover] = mangaUpdate.cover
            it[volume] = Json.encodeToString(mangaUpdate.volume)
        }
    }
}
