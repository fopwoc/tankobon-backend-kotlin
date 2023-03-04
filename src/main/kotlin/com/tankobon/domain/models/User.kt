package com.tankobon.domain.models

import java.util.UUID

data class UserHash(
    val id: UUID,
    val password: String,
)
