package com.tankobon.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object RefreshTokenModel : Table(name = "TOKENS") {
    val uuid = varchar("id", 36)
    val refreshToken = varchar("refreshToken", 64)
    val expires = long("expires")
}

@Serializable
data class TokenPair(
    val instanceId: String,
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class RefreshTokenPayload(
    val refreshToken: String
)

data class RefreshTokenData(
    val uuid: String,
    val refreshToken: String,
    val expires: Long
)

fun ResultRow.toRefreshTokenData() = RefreshTokenData(
    uuid = this[RefreshTokenModel.uuid],
    refreshToken = this[RefreshTokenModel.refreshToken],
    expires = this[RefreshTokenModel.expires]
)
