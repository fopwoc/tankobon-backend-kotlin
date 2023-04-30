package com.tankobon.api.models

import com.tankobon.domain.models.IdEntity
import com.tankobon.domain.models.InstancePrivilegedUpdatable
import com.tankobon.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class InstanceAboutModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val title: String,
    override val description: String,
) : IdEntity<UUID>, InstancePrivilegedUpdatable

@Serializable
data class InstanceAboutUpdatePayloadModel(
    override val title: String,
    override val description: String
) : InstancePrivilegedUpdatable
