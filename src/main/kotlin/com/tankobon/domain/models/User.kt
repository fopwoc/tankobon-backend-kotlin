package com.tankobon.domain.models

import java.util.UUID

// interface UserPrivate {
//    val password: String
// }

interface UserPublic {
    val username: String
}

interface UserUpdatable {
    val username: String
    val password: String
}

interface UserPrivilegedUpdatable {
    val active: Boolean
    val admin: Boolean
}

data class UserCredentials(
    override val id: UUID,
    override val username: String,
    override val password: String
) : IdEntity<UUID>, UserUpdatable
