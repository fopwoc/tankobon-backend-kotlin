package com.tankobon.domain.database.services.manga

import com.tankobon.api.models.MangaPage
import com.tankobon.api.models.MangaVolume
import com.tankobon.domain.database.models.MangaPageModel
import com.tankobon.domain.database.models.MangaVolumeModel
import com.tankobon.domain.database.models.toMangaPage
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.logger
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

suspend fun createMangePage(
    volumeId: UUID,
    pageList: List<MangaPage>,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    pageList.forEachIndexed { i, page ->
        MangaPageModel.insert {
            it[this.id] = page.id
            it[this.volumeId] = volumeId
            it[this.order] = i
            it[this.hash] = page.hash
        }
    }
}

suspend fun deleteMangaPage(
    titleId: UUID,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    val listIds = MangaVolumeModel.select {
        MangaVolumeModel.titleId eq titleId
    }.map { it[MangaVolumeModel.id] }

    listIds.forEach { id ->
        MangaPageModel.deleteWhere { volumeId eq id }
    }
}
