package com.tankobon.api.models

import com.tankobon.domain.models.DateEntity
import com.tankobon.domain.models.IdEntity
import com.tankobon.domain.models.UserPrivilegedUpdatable
import com.tankobon.domain.models.UserPublic
import com.tankobon.domain.models.UserUpdatable
import com.tankobon.utils.UUIDSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val username: String,
    override val admin: Boolean,
    override val active: Boolean,
    override val creation: Instant,
    override val modified: Instant,
) : IdEntity<UUID>, DateEntity<Instant>, UserPublic, UserPrivilegedUpdatable

@Serializable
data class UserCreatePayloadModel(
    override val username: String,
    override val password: String,
    override val admin: Boolean,
    override val active: Boolean? = null,
) : UserUpdatable, UserPrivilegedUpdatable

@Serializable
data class UserLoginPayloadModel(
    override val username: String,
    override val password: String
) : UserUpdatable

@Serializable
data class UserIdPayloadModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID
) : IdEntity<UUID>

@Serializable
data class UserUpdatePayloadModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val username: String,
    override val password: String,
    override val admin: Boolean,
    override val active: Boolean,
) : IdEntity<UUID>, UserPublic, UserPrivilegedUpdatable, UserUpdatable
