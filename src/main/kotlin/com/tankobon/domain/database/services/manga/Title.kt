package com.tankobon.domain.database.services.manga

import com.tankobon.domain.database.models.MangaTitleTable
import com.tankobon.domain.models.MangaUpdate
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.utils.dbQuery
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.io.File
import java.util.UUID

suspend fun doesTitleExists(id: UUID) = dbQuery {
    !MangaTitleTable.select { MangaTitleTable.id eq id }.none()
}
suspend fun createMangeTitle(
    update: MangaUpdate,
) = dbQuery {
    val time = Clock.System.now()
    MangaTitleTable.insert {
        it[this.id] = update.id
        it[this.title] = update.title ?: "unknown"
        it[this.description] = ""
        it[this.cover] = ""
        it[this.created] = time
        it[this.modified] = time
    }
}

suspend fun updateDateMangaTitle(
    id: UUID,
) = dbQuery {
    MangaTitleTable.update({ MangaTitleTable.id eq id }) {
        it[this.modified] = Clock.System.now()
    }
}

suspend fun deleteMangaTitle(
    titleId: UUID,
) = dbQuery {
    MangaTitleTable.deleteWhere { MangaTitleTable.id eq titleId }
    File("${ConfigProvider.get().library.thumbFile.path}/$titleId").deleteRecursively()
}
