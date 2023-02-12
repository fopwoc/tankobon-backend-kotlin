package com.tankobon.domain.database.models

import com.tankobon.api.models.RefreshTokenData
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

private const val REFRESH_TOKEN_MODEL_UUID_LENGTH = 36
private const val REFRESH_TOKEN_MODEL_REFRESH_TOKEN_LENGTH = 64

object RefreshTokenModel : Table(name = "TOKENS") {
    val uuid = varchar("id", REFRESH_TOKEN_MODEL_UUID_LENGTH)
    val refreshToken = varchar("refreshToken", REFRESH_TOKEN_MODEL_REFRESH_TOKEN_LENGTH)
    val expires = long("expires")
}

fun ResultRow.toRefreshTokenData() = RefreshTokenData(
    uuid = this[RefreshTokenModel.uuid],
    refreshToken = this[RefreshTokenModel.refreshToken],
    expires = this[RefreshTokenModel.expires],
)
