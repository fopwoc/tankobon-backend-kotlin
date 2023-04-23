package com.tankobon.domain.models

import java.util.UUID

interface UserPublic {
    val username: String
}

interface UserUpdatable {
    val username: String
    val password: String
}

interface UserPrivilegedUpdatable {
    val admin: Boolean
    val active: Boolean?
}

data class UserCredentials(
    override val id: UUID,
    override val username: String,
    override val password: String
) : IdEntity<UUID>, UserUpdatable
