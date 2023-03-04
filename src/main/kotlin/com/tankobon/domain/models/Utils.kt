package com.tankobon.domain.models

import java.util.UUID

data class Utils(
    val instanceId: UUID,
    val public: String,
    val private: String,
    val creationDate: Long,
)
