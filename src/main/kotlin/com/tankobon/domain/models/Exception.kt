package com.tankobon.domain.models


enum class BackendExceptionType {
    WRONG_CREDENTIALS,
    NOT_ADMIN,
    USER_EXIST,
    UNKNOWN
}

interface ExceptionMessage {
    val type: BackendExceptionType
    val message: String?
}
