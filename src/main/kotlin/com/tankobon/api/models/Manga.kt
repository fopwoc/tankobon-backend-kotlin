package com.tankobon.api.models

import com.tankobon.domain.models.ContentEntity
import com.tankobon.domain.models.DateEntity
import com.tankobon.domain.models.FilterEntity
import com.tankobon.domain.models.IdEntity
import com.tankobon.domain.models.ImageMeta
import com.tankobon.domain.models.MangaTitleMeta
import com.tankobon.domain.models.MangaVolumeMeta
import com.tankobon.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Manga(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val title: String,
    override val description: String,
    override val creation: Long,
    override val modified: Long,
    override val content: List<MangaVolumeModel>
) : IdEntity<UUID>, ContentEntity<MangaVolumeModel>, DateEntity<Long>, MangaTitleMeta

@Serializable
data class MangaTitle(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val title: String,
    override val description: String,
    override val creation: Long,
    override val modified: Long,
) : IdEntity<UUID>, DateEntity<Long>, MangaTitleMeta

@Serializable
data class MangaVolumeModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val title: String?,
    override val content: List<MangaPageModel>,
) : IdEntity<UUID>, ContentEntity<MangaPageModel>, MangaVolumeMeta

@Serializable
data class MangaPageModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
    override val hash: String,
) : IdEntity<UUID>, ImageMeta

@Serializable
data class MangaTitleUpdatePayloadModel(
    override val title: String,
    override val description: String,
) : MangaTitleMeta

@Serializable
data class MangaVolumeUpdatePayloadModel(
    override val title: String,
) : MangaVolumeMeta

@Serializable
data class MangaFilterPayloadModel(
    override val offset: Long?,
    override val limit: Int?,
    override val search: String?,
) : FilterEntity

@Serializable
data class MangaIdPayloadModel(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID,
) : IdEntity<UUID>
