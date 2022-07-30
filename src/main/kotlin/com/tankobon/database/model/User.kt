package com.tankobon.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow

object UserModel : UUIDTable(name = "USERS") {
    val username = varchar("username", 255)
    val password = varchar("password", 64)
    var registerDate = long("registerDate")
    var active = bool("active")
    var admin = bool("admin")
}

// TODO show expire time
@Serializable
data class User(
    val id: String,
    val username: String,
    val registerDate: Long,
    val active: Boolean,
    val admin: Boolean
)

fun ResultRow.toUser(): User = User(
    id = this[UserModel.id].toString(),
    username = this[UserModel.username],
    registerDate = this[UserModel.registerDate],
    active = this[UserModel.active],
    admin = this[UserModel.admin]
)

@Serializable
data class CreateUserPayload(
    val username: String,
    val password: String,
    val admin: Boolean
)

@Serializable
data class UserPayload(
    val username: String,
    val password: String
)

data class UserHash(
    val id: String,
    val password: String
)

fun ResultRow.toUserHash() = UserHash(
    id = this[UserModel.id].toString(),
    password = this[UserModel.password]
)
