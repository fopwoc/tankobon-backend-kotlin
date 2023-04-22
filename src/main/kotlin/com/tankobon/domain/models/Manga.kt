package com.tankobon.domain.models

import com.tankobon.api.models.MangaVolumeModel
import java.util.UUID

interface MangaTitleMeta {
    val title: String
    val description: String
}

interface MangaVolumeMeta {
    val title: String?
}

// TODO: review inheritance
data class MangaUpdate(
    override val id: UUID,
    override val title: String? = null,
    override val content: List<MangaVolumeModel> = emptyList(),
) : IdEntity<UUID>, ContentEntity<MangaVolumeModel>, MangaVolumeMeta
