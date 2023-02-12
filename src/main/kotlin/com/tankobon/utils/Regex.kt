package com.tankobon.utils

import com.tankobon.domain.providers.ConfigProvider

fun isNumber(string: String): Boolean {
    return Regex("^(\\d*)\$").matches(string)
}

fun isTitleDigits(name: String): Boolean {
    return Regex("^\\d{${ConfigProvider.get().library.titleDigits}}\$").matches(name)
}

fun isVolumeDigits(name: String): Boolean {
    return Regex("^\\d{${ConfigProvider.get().library.titleDigits}}\$").matches(name)
}

fun isZipFile(name: String): Boolean {
    return Regex("^(zip|cbz)\$").matches(name)
}

fun isRarFile(name: String): Boolean {
    return Regex("^(rar|cbr)\$").matches(name)
}

fun isPngFile(name: String): Boolean {
    return Regex("^(png|PNG)\$").matches(name)
}

fun isJpgFile(name: String): Boolean {
    return Regex("^(jpg)\$").matches(name)
}
fun isWrongJpgFile(name: String): Boolean {
    return Regex("^(jpeg|jpe|JPEG|JPE|JPG)\$").matches(name)
}
