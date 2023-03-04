package com.tankobon.api.models

import com.tankobon.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

// TODO refactor all models everywhere
interface MangaEntity {
    val id: UUID
}

@Serializable
data class Manga(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val title: String,
    val description: String,
    val cover: String,
    val createdDate: Long,
    val updatedDate: Long,
    val content: List<MangaVolume>
)

@Serializable
data class MangaTitle(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val title: String,
    val description: String,
    val cover: String,
    val createdDate: Long,
    val updatedDate: Long,
)

@Serializable
data class MangaVolume(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    val title: String?,
    val content: List<MangaPage>,
) : MangaEntity

@Serializable
data class MangaPage(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    val hash: String,
) : MangaEntity

@Serializable
data class MangaUpdatePayload(
    val title: String,
    val description: String,
    val cover: String,
)

@Serializable
data class MangaVolumeUpdatePayload(
    val title: String,
)

@Serializable
data class MangaPayload(
    val offset: Long?,
    val limit: Int?,
    val search: String?,
)

@Serializable
data class MangaIdPayload(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
)
