package com.tankobon.domain.database.models

import com.tankobon.api.models.User
import com.tankobon.domain.models.UserHash
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow

private const val USER_MODEL_USERNAME_LENGTH = 255
private const val USER_MODEL_PASSWORD_LENGTH = 64

object UserModel : UUIDTable(name = "USERS") {
    val username = varchar("username", USER_MODEL_USERNAME_LENGTH)
    val password = varchar("password", USER_MODEL_PASSWORD_LENGTH)
    var registerDate = long("registerDate")
    var active = bool("active")
    var admin = bool("admin")
}

fun ResultRow.toUser(): User = User(
    uuid = this[UserModel.id].toString(),
    username = this[UserModel.username],
    registerDate = this[UserModel.registerDate],
    active = this[UserModel.active],
    admin = this[UserModel.admin],
)

fun ResultRow.toUserHash() = UserHash(
    id = this[UserModel.id].toString(),
    password = this[UserModel.password],
)
