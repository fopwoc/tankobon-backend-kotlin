package com.tankobon.domain.models

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

interface TokenMeta : DateEntity<Long> {
    val userAgent: String
    val userIP: String
}

data class TokenData(
    override val id: UUID,
    override val userId: UUID,
    override val userAgent: String,
    override val userIP: String,
    override val refreshToken: String,
    override val creation: Long,
    override val modified: Long
) : IdEntity<UUID>, TokenUserId, TokenRefresh, TokenMeta
