package com.tankobon.utils

import java.util.UUID
import java.util.regex.Pattern

private val uuidRegexPattern: Pattern =
    Pattern.compile("^[{]?[\\da-fA-F]{8}-([\\da-fA-F]{4}-){3}[\\da-fA-F]{12}[}]?$")

fun isValidUUID(str: String?): Boolean {
    return if (str == null) {
        false
    } else {
        uuidRegexPattern.matcher(str).matches()
    }
}

fun uuidFromString(str: String?): UUID? {
    return if (str == null) {
        null
    } else {
        if (uuidRegexPattern.matcher(str).matches()) {
            UUID.fromString(str)
        } else {
            null
        }
    }
}
