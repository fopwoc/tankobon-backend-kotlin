package com.tankobon.domain.database.services.manga

import com.tankobon.domain.database.models.MangaTitleModel
import com.tankobon.domain.models.MangaUpdate
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.io.File
import java.util.UUID

suspend fun doesTitleExists(id: UUID) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    !MangaTitleModel.select { MangaTitleModel.id eq id }.none()
}
suspend fun createMangeTitle(
    update: MangaUpdate,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    MangaTitleModel.insert {
        it[this.id] = update.id
        it[this.title] = update.title ?: "unknown"
        it[this.description] = ""
        it[this.cover] = ""
        it[this.createdDate] = System.currentTimeMillis()
        it[this.updatedDate] = System.currentTimeMillis()
    }
}

suspend fun updateDateMangaTitle(
    id: UUID,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    MangaTitleModel.update({ MangaTitleModel.id eq id }) {
        it[this.updatedDate] = System.currentTimeMillis()
    }
}

suspend fun deleteMangaTitle(
    titleId: UUID,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    MangaTitleModel.deleteWhere { MangaTitleModel.id eq titleId }
    File("${ConfigProvider.get().library.thumbFile.path}/$titleId").deleteRecursively()
}
