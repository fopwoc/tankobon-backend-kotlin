package com.tankobon.domain.database.services.manga

import com.tankobon.api.models.MangaVolume
import com.tankobon.domain.database.models.MangaVolumeModel
import com.tankobon.domain.models.MangaUpdate
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.logger
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

suspend fun createMangeVolume(
    titleId: UUID,
    volumeList: List<MangaVolume>,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    volumeList.forEachIndexed { i, volume ->
        MangaVolumeModel.insert {
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
    MangaVolumeModel.deleteWhere { MangaVolumeModel.titleId eq titleId }
}
