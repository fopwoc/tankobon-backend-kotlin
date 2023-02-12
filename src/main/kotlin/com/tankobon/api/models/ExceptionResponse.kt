package com.tankobon.api.models

import kotlinx.serialization.Serializable

@Serializable
data class ExceptionResponse(
    val type: String,
    val message: String?,
)
