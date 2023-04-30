package com.tankobon.api.models

import com.tankobon.domain.models.DateEntity
import com.tankobon.domain.models.IdEntity
import com.tankobon.domain.models.LastPointMeta
import com.tankobon.utils.UUIDSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LastPointModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    override val volumeId: UUID,
    @Serializable(with = UUIDSerializer::class)
    override val pageId: UUID,
    override val creation: Instant,
    override val modified: Instant,
) : IdEntity<UUID>, DateEntity<Instant>, LastPointMeta
