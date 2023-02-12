package com.tankobon.utils

fun formatDigits(index: Int, length: Int): String {
    return "%0${length}d".format(index)
}
