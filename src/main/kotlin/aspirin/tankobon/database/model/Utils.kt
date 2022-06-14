package aspirin.tankobon.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object UtilsModel : Table() {
    val public = varchar("public", 512)
    val private = varchar("private", 2048)
    var creationDate = long("creationDate")
}

@Serializable
data class Utils(
    val public: String,
    val private: String,
    val creationDate: Long,
)
