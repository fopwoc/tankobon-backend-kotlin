package aspirin.tankobon.database.service

import aspirin.tankobon.database.model.Manga
import aspirin.tankobon.database.model.MangaModel
import aspirin.tankobon.database.model.MangaUpdate
import aspirin.tankobon.globalThumbPath
import aspirin.tankobon.utils.intListUtils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File
import java.util.*

class MangaService(val database: Database) {

    suspend fun getMangaList(): List<Manga> = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction MangaModel.selectAll().map { toManga(it) }
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
        val currentList = MangaModel.selectAll().map { toManga(it) }

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

    private fun toManga(row: ResultRow): Manga {
        return Manga(
            id = row[MangaModel.id].toString(),
            title = row[MangaModel.title],
            description = row[MangaModel.description],
            cover = row[MangaModel.cover],
            volume = intListUtils(row[MangaModel.volume]),
        )
    }

}