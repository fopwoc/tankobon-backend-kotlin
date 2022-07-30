package com.tankobon.database.model

import com.tankobon.utils.intListUtils
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow

object MangaModel : UUIDTable(name = "MANGA") {
    val title = varchar("title", 255)
    val description = text("description")
    val cover = text("cover")
    val volume = text("volume")
}

@Serializable
data class Manga(
    val id: String,
    val title: String,
    val description: String,
    val cover: String,
    val volume: List<Int>
)

fun ResultRow.toManga() = Manga(
    id = this[MangaModel.id].toString(),
    title = this[MangaModel.title],
    description = this[MangaModel.description],
    cover = this[MangaModel.cover],
    volume = intListUtils(this[MangaModel.volume])
)

data class MangaUpdate(
    val id: String,
    val title: String?,
    val volume: List<Int>
)

@Serializable
data class MangaUpdatePayload(
    val title: String,
    val description: String,
    val cover: String,
)

@Serializable
data class MangaPayload(
    val offset: Long?,
    val limit: Int?,
    val search: String?,
)

@Serializable
data class MangaIdPayload(
    val id: String,
)