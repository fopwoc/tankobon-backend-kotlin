package com.tankobon.api.models

import kotlinx.serialization.Serializable

// TODO show expire time
@Serializable
data class User(
    val uuid: String,
    val username: String,
    val registerDate: Long,
    val active: Boolean,
    val admin: Boolean,
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
