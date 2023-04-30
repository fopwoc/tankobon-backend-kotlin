package com.tankobon.domain.models

import com.tankobon.utils.UUIDSerializer
import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface LastPointMeta {
    val volumeId: UUID
    val pageId: UUID
}

interface LastPointPrivate {
    val userId: UUID
}
