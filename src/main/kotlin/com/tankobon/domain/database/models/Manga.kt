package com.tankobon.domain.database.models

import com.tankobon.api.models.Manga
import com.tankobon.api.models.MangaPageModel
import com.tankobon.api.models.MangaTitle
import com.tankobon.api.models.MangaVolumeModel
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

private const val MANGA_LIBRARY_MODEL_TITLE_LENGTH = 512
private const val MANGA_LIBRARY_PAGE_MODEL_HASH_LENGTH = 24

object MangaTitleTable : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.manga_title"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val title = varchar("title", MANGA_LIBRARY_MODEL_TITLE_LENGTH)
    val description = text("description")
    val cover = text("cover")
    val created = long("created")
    val modified = long("modified")
}

object MangaVolumeTable : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.manga_volume"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val titleId = reference("title_id", MangaTitleTable)
    val order = integer("order")
    val title = text("title")
}

object MangaPageTable : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.manga_page"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val volumeId = reference("volume_id", MangaVolumeTable)
    val order = integer("order")
    val hash = varchar("hash", MANGA_LIBRARY_PAGE_MODEL_HASH_LENGTH)
}

fun ResultRow.toManga() = Manga(
    id = this[MangaTitleTable.id].value,
    title = this[MangaTitleTable.title],
    description = this[MangaTitleTable.description],
    creation = this[MangaTitleTable.created],
    modified = this[MangaTitleTable.modified],
    content = listOf()
)

fun ResultRow.toMangaTitle() = MangaTitle(
    id = this[MangaTitleTable.id].value,
    title = this[MangaTitleTable.title],
    description = this[MangaTitleTable.description],
    creation = this[MangaTitleTable.created],
    modified = this[MangaTitleTable.modified],
)

fun ResultRow.toMangaVolume() = MangaVolumeModel(
    id = this[MangaVolumeTable.id].value,
    title = this[MangaVolumeTable.title],
    content = listOf()
)

fun ResultRow.toMangaPage() = MangaPageModel(
    id = this[MangaPageTable.id].value,
    hash = this[MangaPageTable.hash],
)
