package com.tankobon.domain.database.models

import com.tankobon.domain.models.TokenData
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

private const val REFRESH_TOKEN_MODEL_REFRESH_TOKEN_LENGTH = 64
private const val REFRESH_TOKEN_MODEL_IP_LENGTH = 15

object RefreshTokenTable : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.tokens"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val userId = reference("user_id", UserTable)
    val userAgent = text("user_agent")
    val userIP = varchar("user_ip", REFRESH_TOKEN_MODEL_IP_LENGTH)
    val refreshToken = varchar("refreshToken", REFRESH_TOKEN_MODEL_REFRESH_TOKEN_LENGTH)
    val expires = long("expires")
}

fun ResultRow.toRefreshTokenData() = TokenData(
    id = this[RefreshTokenTable.id].value,
    userId = this[RefreshTokenTable.userId].value,
    userAgent = this[RefreshTokenTable.userAgent],
    userIP = this[RefreshTokenTable.userIP],
    refreshToken = this[RefreshTokenTable.refreshToken],
    expires = this[RefreshTokenTable.expires],
)
