package com.tankobon.domain.database.models

import com.tankobon.api.models.Manga
import com.tankobon.api.models.MangaPage
import com.tankobon.api.models.MangaTitle
import com.tankobon.api.models.MangaVolume
import com.tankobon.domain.database.models.MangaPageModel.entityId
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

private const val MANGA_LIBRARY_MODEL_TITLE_LENGTH = 512
private const val MANGA_LIBRARY_PAGE_MODEL_HASH_LENGTH = 24

object MangaTitleModel : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.manga_title"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val title = varchar("title", MANGA_LIBRARY_MODEL_TITLE_LENGTH)
    val description = text("description")
    val cover = text("cover")
    val createdDate = long("created_date")
    val updatedDate = long("updated_date")
}

object MangaVolumeModel : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.manga_volume"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val titleId = reference("title_id", MangaTitleModel)
    val order = integer("order")
    val title = text("title")
}

object MangaPageModel : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.manga_page"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val volumeId = reference("volume_id", MangaVolumeModel)
    val order = integer("order")
    val hash = varchar("hash", MANGA_LIBRARY_PAGE_MODEL_HASH_LENGTH)
}

fun ResultRow.toMangaTitle() = MangaTitle(
    id = this[MangaTitleModel.id].value,
    title = this[MangaTitleModel.title],
    description = this[MangaTitleModel.description],
    cover = this[MangaTitleModel.cover],
    createdDate = this[MangaTitleModel.createdDate],
    updatedDate = this[MangaTitleModel.updatedDate],
)

fun ResultRow.toManga() = Manga(
    id = this[MangaTitleModel.id].value,
    title = this[MangaTitleModel.title],
    description = this[MangaTitleModel.description],
    cover = this[MangaTitleModel.cover],
    createdDate = this[MangaTitleModel.createdDate],
    updatedDate = this[MangaTitleModel.updatedDate],
    content = listOf()
)

fun ResultRow.toMangaVolume() = MangaVolume(
    id = this[MangaVolumeModel.id].value,
    title = this[MangaVolumeModel.title],
    content = listOf()
)

fun ResultRow.toMangaPage() = MangaPage(
    id = this[MangaPageModel.id].value,
    hash = this[MangaPageModel.hash],
)
