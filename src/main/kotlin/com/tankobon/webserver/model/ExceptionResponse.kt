package com.tankobon.webserver.model

import kotlinx.serialization.Serializable

@Serializable
data class ExceptionResponse(
    val type: String,
    val message: String?,
)
