package com.tankobon.api.models

import kotlinx.serialization.Serializable

@Serializable
data class Manga(
    val uuid: String,
    val title: String,
    val description: String,
    val cover: String,
    val volume: List<MangaVolume>,
)

@Serializable
data class MangaVolume(
    val order: Int,
    val title: String?,
    val content: List<String>,
)

@Serializable
data class MangaUpdatePayload(
    val title: String,
    val description: String,
    val cover: String,
)

@Serializable
data class MangaPayload(
    val offset: Long?,
    val limit: Int?,
    val search: String?,
)

@Serializable
data class MangaIdPayload(
    val id: String,
)
