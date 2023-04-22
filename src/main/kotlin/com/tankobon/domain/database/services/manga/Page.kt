package com.tankobon.domain.database.services.manga

import com.tankobon.api.models.MangaPageModel
import com.tankobon.domain.database.models.MangaPageTable
import com.tankobon.domain.database.models.MangaVolumeTable
import com.tankobon.domain.providers.DatabaseProvider
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

suspend fun createMangePage(
    volumeId: UUID,
    pageList: List<MangaPageModel>,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    pageList.forEachIndexed { i, page ->
        MangaPageTable.insert {
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
    val listIds = MangaVolumeTable.select {
        MangaVolumeTable.titleId eq titleId
    }.map { it[MangaVolumeTable.id] }

    listIds.forEach { id ->
        MangaPageTable.deleteWhere { volumeId eq id }
    }
}
