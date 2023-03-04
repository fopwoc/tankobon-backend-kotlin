package com.tankobon.api.models

import com.tankobon.utils.UUIDSerializer
import java.util.UUID
import kotlinx.serialization.Serializable

// TODO show expire time
@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val username: String,
    val active: Boolean,
    val admin: Boolean,
    val created: Long,
    val modified: Long,
)

@Serializable
data class CreateUserPayload(
    val username: String,
    val password: String,
    val admin: Boolean,
)

@Serializable
data class UserPayload(
    val username: String,
    val password: String,
)
