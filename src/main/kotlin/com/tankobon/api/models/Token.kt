package com.tankobon.api.models

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenPayload(
    val refreshToken: String,
)

data class RefreshTokenData(
    val uuid: String,
    val refreshToken: String,
    val expires: Long,
)

@Serializable
data class TokenPair(
    val instanceId: String,
    val accessToken: String,
    val refreshToken: String,
)
