package com.tankobon.api.models

import kotlinx.serialization.Serializable

@Serializable
data class UtilsAbout(
    val instanceName: String,
    val instanceDescription: String,
    val creationDate: Long,
)

@Serializable
data class UtilsAboutUpdatePayload(
    val instanceName: String,
    val instanceDescription: String,
)
