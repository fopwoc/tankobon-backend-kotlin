package com.tankobon.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object RefreshTokenModel : Table() {
    val uuid = varchar("id", 36)
    val refreshToken = varchar("refreshToken", 64)
    val expires = long("expires")
}

@Serializable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class RefreshToken(
    val refreshToken: String,
)

@Serializable
data class RefreshTokenData(
    val uuid: String,
    val refreshToken: String,
    val expires: Long,
)
