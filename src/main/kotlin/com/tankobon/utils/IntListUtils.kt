package com.tankobon.utils

fun intListUtils(str: String): List<Int> {
    return str.removeSurrounding("[", "]")
        .takeIf(String::isNotEmpty)
        ?.split(",")?.map { Integer.parseInt(it) }
        .orEmpty()
}
