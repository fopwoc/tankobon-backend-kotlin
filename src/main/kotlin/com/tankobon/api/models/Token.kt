package com.tankobon.api.models

import com.tankobon.domain.models.IdEntity
import com.tankobon.domain.models.TokenAccess
import com.tankobon.domain.models.TokenInstanceId
import com.tankobon.domain.models.TokenMeta
import com.tankobon.domain.models.TokenRefresh
import com.tankobon.utils.UUIDSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RefreshTokenPayloadModel(
    override val refreshToken: String,
) : TokenRefresh

@Serializable
data class TokenIdPayloadModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID
) : IdEntity<UUID>

@Serializable
data class TokenPairModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    override val instanceId: UUID,
    override val accessToken: String,
    override val refreshToken: String,
) : IdEntity<UUID>, TokenInstanceId, TokenAccess, TokenRefresh

@Serializable
data class TokenInfoModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val userAgent: String,
    override val userIP: String,
    override val creation: Instant,
    override val modified: Instant
) : IdEntity<UUID>, TokenMeta
