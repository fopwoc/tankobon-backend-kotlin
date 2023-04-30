package com.tankobon.domain.models

import kotlinx.datetime.Instant
import java.util.UUID

interface InstancePrivate {
    val publicKey: String
    val privateKey: String
}

interface InstancePrivilegedUpdatable {
    val title: String
    val description: String
}

interface InstanceEntity : InstancePrivate, InstancePrivilegedUpdatable

data class Instance(
    override val id: UUID,
    override val publicKey: String,
    override val privateKey: String,
    override val title: String,
    override val description: String,
    override val creation: Instant,
    override val modified: Instant,
) : IdEntity<UUID>, DateEntity<Instant>, InstanceEntity
