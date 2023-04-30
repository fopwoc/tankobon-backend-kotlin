package com.tankobon.domain.database.services

import com.tankobon.api.models.LastPointModel
import com.tankobon.domain.database.models.LastPointTable
import com.tankobon.domain.database.models.toLastPointModel
import com.tankobon.utils.dbQuery
import com.tankobon.utils.injectLogger
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.UUID

class LastPointService {

    companion object {
        val log by injectLogger()
    }

    suspend fun getAllLastPoints(userId: UUID): List<LastPointModel> = dbQuery {
        LastPointTable.select { LastPointTable.userId eq userId }.map { it.toLastPointModel() }.toList()
    }

    suspend fun getLastPoint(id: UUID, userId: UUID): LastPointModel? = dbQuery {
        return@dbQuery LastPointTable.select { LastPointTable.id eq id and(LastPointTable.userId eq userId) }
            .firstOrNull()?.toLastPointModel()
    }

    suspend fun setLastPoint(id: UUID, volumeId: UUID, pageId: UUID, userId: UUID) = dbQuery {
        val lastPoint = getLastPoint(id, userId)

        if (lastPoint == null) {
            val time = Clock.System.now()
            LastPointTable.insert {
                it[this.id] = id
                it[this.volumeId] = volumeId
                it[this.pageId] = pageId
                it[this.userId] = userId
                it[this.created] = time
                it[this.modified] = time
            }
        } else {
            LastPointTable.update({ LastPointTable.id eq id and(LastPointTable.userId eq userId) }) {
                it[this.volumeId] = volumeId
                it[this.pageId] = pageId
                it[this.modified] = Clock.System.now()
            }
        }
    }
}
