package aspirin.tankobon.database.service

import aspirin.tankobon.database.model.Utils
import aspirin.tankobon.database.model.UtilsModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UtilService(val database: Database) {

    fun getSecret(): String = transaction(db = database) {
        UtilsModel.selectAll().map { toUtils(it).secret }.first()
    }

    private fun toUtils(row: ResultRow): Utils {
        return Utils(
            secret = row[UtilsModel.secret],
            creationDate = row[UtilsModel.creationDate]
        )
    }
}
