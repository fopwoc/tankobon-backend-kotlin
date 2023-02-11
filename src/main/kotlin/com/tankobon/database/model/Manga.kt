package com.tankobon.database.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

object MangaLibraryModel : UUIDTable(name = "MANGA") {
    val title = varchar("title", 255)
    val description = text("description")
    val cover = text("cover")

    /* / FIXME by architecture, this is broken solution in case
    *     that i want to change names of volumes not every time i
    *     caught update in watch service.
    *     Also not really convenient to change specific volume titles
    *     with possible REST
    *     And now this is heavy json if i trying to get all
    *     manga list by /manga REST
    *     ГОВНО ПЕРЕДЕЛЫВАЙ! */
    val volume = text("volume")
}

@Serializable
data class Manga(
    val id: String,
    val title: String,
    val description: String,
    val cover: String,
    val volume: List<MangaVolume>
)

fun ResultRow.toManga() = Manga(
    id = this[MangaLibraryModel.id].toString(),
    title = this[MangaLibraryModel.title],
    description = this[MangaLibraryModel.description],
    cover = this[MangaLibraryModel.cover],
    volume = Json.decodeFromString(this[MangaLibraryModel.volume])
)

// TODO refactor db models, local models and payload models to their specific folders
data class MangaUpdate(
    // TODO all id to uuid everywhere!
    val id: UUID,
    val title: String?,
    val volume: List<MangaVolume>
)

@Serializable
data class MangaVolume(
    val order: Int,
    val title: String?,
    val content: List<String>,
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
