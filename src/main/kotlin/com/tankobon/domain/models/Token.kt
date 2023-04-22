package com.tankobon.domain.models

import java.util.UUID

interface TokenAccess {
    val accessToken: String
}

interface TokenRefresh {
    val refreshToken: String
}

interface TokenMeta {
    val userId: UUID
    val userAgent: String
    val userIP: String
    val expires: Long
}

data class TokenData(
    override val id: UUID,
    override val userId: UUID,
    override val userAgent: String,
    override val userIP: String,
    override val refreshToken: String,
    override val expires: Long
) : IdEntity<UUID>, TokenRefresh, TokenMeta
