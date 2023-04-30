package com.tankobon.utils

import java.time.Duration
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

fun msToPrettyTime(time: Long): String {
    return "${msToMinutes(time)}:${msToRemainderSeconds(time)}:${msToRemainderMs(time)}"
}

private fun msToMinutes(time: Long): Long {
    return time / Duration.ofSeconds(1).toMillis() / Duration.ofMinutes(1).toSeconds()
}

private fun msToRemainderSeconds(time: Long): Long {
    return time / Duration.ofSeconds(1).toMillis() % Duration.ofMinutes(1).toSeconds()
}

private fun msToRemainderMs(time: Long): Long {
    return time % Duration.ofSeconds(1).toMillis()
}
