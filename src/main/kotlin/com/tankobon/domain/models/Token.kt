package com.tankobon.domain.models

import java.util.UUID

data class RefreshTokenData(
    val id: UUID,
    val userId: UUID,
    val userAgent: String,
    val refreshToken: String,
    val expires: Long,
)
