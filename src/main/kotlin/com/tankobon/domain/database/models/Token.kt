package com.tankobon.domain.database.models

import com.tankobon.api.models.TokenInfoModel
import com.tankobon.domain.models.TokenData
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

private const val REFRESH_TOKEN_MODEL_REFRESH_TOKEN_LENGTH = 64
private const val REFRESH_TOKEN_MODEL_IP_LENGTH = 15

object TokenTable : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.tokens"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val userId = reference("user_id", UserTable)
    val userAgent = text("user_agent")
    val userIP = varchar("user_ip", REFRESH_TOKEN_MODEL_IP_LENGTH)
    val refreshToken = varchar("refreshToken", REFRESH_TOKEN_MODEL_REFRESH_TOKEN_LENGTH)
    val creation = timestamp("creation")
    val modified = timestamp("modified")
}

fun ResultRow.toRefreshTokenData() = TokenData(
    id = this[TokenTable.id].value,
    userId = this[TokenTable.userId].value,
    userAgent = this[TokenTable.userAgent],
    userIP = this[TokenTable.userIP],
    refreshToken = this[TokenTable.refreshToken],
    creation = this[TokenTable.creation],
    modified = this[TokenTable.modified],
)

fun ResultRow.toTokenInfo() = TokenInfoModel(
    id = this[TokenTable.id].value,
    userAgent = this[TokenTable.userAgent],
    userIP = this[TokenTable.userIP],
    creation = this[TokenTable.creation],
    modified = this[TokenTable.modified],
)
