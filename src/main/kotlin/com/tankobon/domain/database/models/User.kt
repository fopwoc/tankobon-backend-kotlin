package com.tankobon.domain.database.models

import com.tankobon.api.models.UserModel
import com.tankobon.domain.models.UserCredentials
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

private const val USER_MODEL_USERNAME_LENGTH = 255
private const val USER_MODEL_PASSWORD_LENGTH = 64

object UserTable : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.users",
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val username = varchar("username", USER_MODEL_USERNAME_LENGTH)
    val password = varchar("password", USER_MODEL_PASSWORD_LENGTH)
    val active = bool("active")
    val admin = bool("admin")
    val creation = timestamp("created")
    val modified = timestamp("modified")
}

fun ResultRow.toUser(): UserModel = UserModel(
    id = this[UserTable.id].value,
    username = this[UserTable.username],
    active = this[UserTable.active],
    admin = this[UserTable.admin],
    creation = this[UserTable.creation],
    modified = this[UserTable.modified],
)

fun ResultRow.toUserCredentials() = UserCredentials(
    id = this[UserTable.id].value,
    username = this[UserTable.username],
    password = this[UserTable.password],
    admin = this[UserTable.admin],
    active = this[UserTable.active],
)
