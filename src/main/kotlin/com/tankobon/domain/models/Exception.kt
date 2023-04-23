package com.tankobon.domain.models

enum class BackendExceptionType {
    WRONG_CREDENTIALS,
    NOT_ADMIN,
    USER_EXIST,
    USER_DISABLED,
    UNKNOWN
}

interface ExceptionMessage {
    val type: BackendExceptionType
    val message: String?
}
