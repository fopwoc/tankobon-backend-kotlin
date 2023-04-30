package com.tankobon.domain.models

import kotlinx.datetime.Instant
import java.util.UUID

enum class TokenClaim {
    TOKEN_ID,
    USER_ID,
}

interface TokenAccess {
    val accessToken: String
}

interface TokenRefresh {
    val refreshToken: String
}

interface TokenUserId {
    val userId: UUID
}

interface TokenInstanceId {
    val instanceId: UUID
}

interface TokenMeta : DateEntity<Instant> {
    val userAgent: String
    val userIP: String
}

data class TokenData(
    override val id: UUID,
    override val userId: UUID,
    override val userAgent: String,
    override val userIP: String,
    override val refreshToken: String,
    override val creation: Instant,
    override val modified: Instant
) : IdEntity<UUID>, TokenUserId, TokenRefresh, TokenMeta
