package com.tankobon.api.models

import com.tankobon.domain.models.DateEntity
import com.tankobon.domain.models.IdEntity
import com.tankobon.domain.models.UserPrivilegedUpdatable
import com.tankobon.domain.models.UserPublic
import com.tankobon.domain.models.UserUpdatable
import com.tankobon.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

// TODO: show expire time
@Serializable
data class UserModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val username: String,
    override val active: Boolean,
    override val admin: Boolean,
    override val creation: Long,
    override val modified: Long,
) : IdEntity<UUID>, DateEntity<Long>, UserPublic, UserPrivilegedUpdatable

@Serializable
data class CreateUserPayloadModel(
    override val username: String,
    override val password: String,
    override val admin: Boolean,
    override val active: Boolean,
) : UserUpdatable, UserPrivilegedUpdatable

@Serializable
data class UserLoginPayloadModel(
    override val username: String,
    override val password: String
) : UserUpdatable


