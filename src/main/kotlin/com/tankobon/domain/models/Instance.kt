package com.tankobon.domain.models

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
    override val creation: Long,
    override val modified: Long,
) : IdEntity<UUID>, DateEntity<Long>, InstanceEntity
