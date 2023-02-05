package com.tankobon.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatTime(time: LocalDateTime = LocalDateTime.now()): String {
    val log = logger("format-time")
    log.trace("")

    val format = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")
    val result = time.format(format)

    log.trace("$time formatted to $result")

    return result
}
