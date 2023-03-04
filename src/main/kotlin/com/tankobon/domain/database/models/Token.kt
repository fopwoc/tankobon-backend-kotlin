package com.tankobon.domain.database.models

import com.tankobon.domain.models.RefreshTokenData
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID
import org.jetbrains.exposed.dao.id.UUIDTable

private const val REFRESH_TOKEN_MODEL_REFRESH_TOKEN_LENGTH = 64

object RefreshTokenModel : UUIDTable(
    name = "${ConfigProvider.get().database.schema}.tokens"
) {
    val userId = reference("user_id", UserModel)
    val userAgent = text("user_agent")
    val refreshToken = varchar("refreshToken", REFRESH_TOKEN_MODEL_REFRESH_TOKEN_LENGTH)
    val expires = long("expires")
}

fun ResultRow.toRefreshTokenData() = RefreshTokenData(
    id = this[RefreshTokenModel.id].value,
    userId = this[RefreshTokenModel.userId].value,
    userAgent = this[RefreshTokenModel.userAgent],
    refreshToken = this[RefreshTokenModel.refreshToken],
    expires = this[RefreshTokenModel.expires],
)
