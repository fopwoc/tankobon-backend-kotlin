package com.tankobon.domain.models

import com.tankobon.api.models.MangaVolume
import java.util.UUID

data class MangaUpdate(
    val id: UUID,
    val title: String? = null,
    val volume: List<MangaVolume> = emptyList(),
)
