package com.tankobon.utils

fun msOffsetDays(days: Int): Long {
    return (days * 24 * 60 * 60 * 1000).toLong()
}
