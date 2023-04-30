package com.tankobon.api.models

import com.tankobon.domain.models.BackendExceptionType
import com.tankobon.domain.models.ExceptionMessage
import kotlinx.serialization.Serializable

@Serializable
data class ExceptionMessageModel(
    override val type: BackendExceptionType,
    override val message: String?,
) : ExceptionMessage
