package com.tankobon.domain.database.services.manga

import com.tankobon.api.models.MangaVolumeModel
import com.tankobon.domain.database.models.MangaVolumeTable
import com.tankobon.domain.providers.DatabaseProvider
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

suspend fun createMangeVolume(
    titleId: UUID,
    volumeList: List<MangaVolumeModel>,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    volumeList.forEachIndexed { i, volume ->
        MangaVolumeTable.insert {
            it[this.id] = volume.id
            it[this.titleId] = titleId
            it[this.order] = i
            it[this.title] = volume.title ?: ""
        }
    }
}

suspend fun deleteMangaVolume(
    titleId: UUID,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    MangaVolumeTable.deleteWhere { MangaVolumeTable.titleId eq titleId }
}
