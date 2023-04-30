package com.tankobon.domain.database.models

import com.tankobon.api.models.LastPointModel
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

object LastPointTable : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.manga_last_point"
) {
    override val id = reference("title_id", MangaTitleTable)
    override val primaryKey = PrimaryKey(id)

    val volumeId = reference("volume_id", MangaVolumeTable)
    val pageId = reference("page_id", MangaPageTable)
    val userId = reference("user_id", UserTable)
    val created = timestamp("created")
    val modified = timestamp("modified")
}

fun ResultRow.toLastPointModel() = LastPointModel(
    id = this[LastPointTable.id].value,
    volumeId = this[LastPointTable.volumeId].value,
    pageId = this[LastPointTable.pageId].value,
    creation = this[LastPointTable.created],
    modified = this[LastPointTable.modified],
)
