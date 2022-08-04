package com.tankobon.utils

import java.util.concurrent.TimeUnit

fun msOffsetDays(days: Int): Long {
    return TimeUnit.MILLISECONDS.convert(days.toLong(), TimeUnit.DAYS)
}
