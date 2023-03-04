package com.tankobon.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

fun formatTime(time: LocalDateTime = LocalDateTime.now()): String {
    val log = logger("format-time")
    log.trace("")

    val format = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")
    val result = time.format(format)

    log.trace("$time formatted to $result")

    return result
}

fun msToMinutes(time: Long): Long {
    return time / Duration.ofSeconds(1).toMillis() / Duration.ofMinutes(1).toSeconds()
}

fun msToRemainderSeconds(time: Long): Long {
    return time / Duration.ofSeconds(1).toMillis() % Duration.ofMinutes(1).toSeconds()
}

fun msToRemainderMs(time: Long): Long {
    return time % Duration.ofSeconds(1).toMillis()
}
