package com.tankobon.domain.database.models

import com.tankobon.api.models.Manga
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow

private const val MANGA_LIBRARY_MODEL_TITLE_LENGTH = 255

object MangaLibraryModel : UUIDTable(name = "MANGA") {
    val title = varchar("title", MANGA_LIBRARY_MODEL_TITLE_LENGTH)
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

fun ResultRow.toManga() = Manga(
    uuid = this[MangaLibraryModel.id].toString(),
    title = this[MangaLibraryModel.title],
    description = this[MangaLibraryModel.description],
    cover = this[MangaLibraryModel.cover],
    volume = Json.decodeFromString(this[MangaLibraryModel.volume]),
)
