package aspirin.tankobon.utils

import java.util.regex.Pattern

private val UUID_REGEX_PATTERN: Pattern =
    Pattern.compile("^[{]?[\\da-fA-F]{8}-([\\da-fA-F]{4}-){3}[\\da-fA-F]{12}[}]?$")

fun isValidUUID(str: String?): Boolean {
    return if (str == null) {
        false
    } else UUID_REGEX_PATTERN.matcher(str).matches()
}