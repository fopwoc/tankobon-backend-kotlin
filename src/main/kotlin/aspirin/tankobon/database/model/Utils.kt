package aspirin.tankobon.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object UtilsModel : Table() {
    val secret = varchar("secret", 36)
    var creationDate = long("creationDate")
}

@Serializable
data class Utils(
    val secret: String,
    val creationDate: Long,
)