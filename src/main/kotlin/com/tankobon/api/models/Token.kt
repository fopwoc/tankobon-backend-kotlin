package com.tankobon.api.models

import com.tankobon.domain.models.IdEntity
import com.tankobon.domain.models.TokenAccess
import com.tankobon.domain.models.TokenRefresh
import com.tankobon.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RefreshTokenPayloadModel(
    val refreshToken: String,
)

@Serializable
data class TokenPairModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val accessToken: String,
    override val refreshToken: String,
) : IdEntity<UUID>, TokenAccess, TokenRefresh
