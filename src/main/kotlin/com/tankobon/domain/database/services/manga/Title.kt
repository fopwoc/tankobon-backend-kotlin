package com.tankobon.domain.database.services.manga

import com.tankobon.domain.database.models.MangaTitleTable
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
    !MangaTitleTable.select { MangaTitleTable.id eq id }.none()
}
suspend fun createMangeTitle(
    update: MangaUpdate,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    MangaTitleTable.insert {
        it[this.id] = update.id
        it[this.title] = update.title ?: "unknown"
        it[this.description] = ""
        it[this.cover] = ""
        it[this.created] = System.currentTimeMillis()
        it[this.modified] = System.currentTimeMillis()
    }
}

suspend fun updateDateMangaTitle(
    id: UUID,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    MangaTitleTable.update({ MangaTitleTable.id eq id }) {
        it[this.created] = System.currentTimeMillis()
    }
}

suspend fun deleteMangaTitle(
    titleId: UUID,
) = newSuspendedTransaction(db = DatabaseProvider.get()) {
    MangaTitleTable.deleteWhere { MangaTitleTable.id eq titleId }
    File("${ConfigProvider.get().library.thumbFile.path}/$titleId").deleteRecursively()
}
