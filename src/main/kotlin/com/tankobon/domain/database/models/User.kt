package com.tankobon.domain.database.models

import com.tankobon.api.models.User
import com.tankobon.domain.models.UserHash
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow

private const val USER_MODEL_USERNAME_LENGTH = 255
private const val USER_MODEL_PASSWORD_LENGTH = 64

object UserModel : UUIDTable(
    name = "${ConfigProvider.get().database.schema}.users",
) {
    val username = varchar("username", USER_MODEL_USERNAME_LENGTH)
    val password = varchar("password", USER_MODEL_PASSWORD_LENGTH)
    val avatar = uuid("avatar").nullable()
    var active = bool("active")
    var admin = bool("admin")
    var created = long("created")
    val modified = long("modified")
}

fun ResultRow.toUser(): User = User(
    id = this[UserModel.id].value,
    username = this[UserModel.username],
    active = this[UserModel.active],
    admin = this[UserModel.admin],
    created = this[UserModel.created],
    modified = this[UserModel.modified],
)

fun ResultRow.toUserHash() = UserHash(
    id = this[UserModel.id].value,
    password = this[UserModel.password],
)
