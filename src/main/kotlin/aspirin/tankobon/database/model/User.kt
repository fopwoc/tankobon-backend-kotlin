package aspirin.tankobon.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable

object UserModel : UUIDTable() {
    val username = varchar("username", 255)
    val password = varchar("password", 64)
    var registerDate = long("registerDate")
    var active = bool("active")
    var admin = bool("admin")
}

@Serializable
data class User(
    val id: String,
    val username: String,
    val registerDate: Long,
    val active: Boolean,
    val admin: Boolean,
)

@Serializable
data class UserNew(
    val username: String,
    val password: String,
    val admin: Boolean,
)

@Serializable
data class UserAuth(
    val username: String,
    val password: String,
)