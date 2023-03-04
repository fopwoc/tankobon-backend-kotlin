package com.tankobon.api.models

import com.tankobon.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RefreshTokenPayload(
    val refreshToken: String,
)

@Serializable
data class TokenPair(
    @Serializable(with = UUIDSerializer::class)
    val instanceId: UUID,
    val accessToken: String,
    val refreshToken: String,
)
