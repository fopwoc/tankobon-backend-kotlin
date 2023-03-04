package com.tankobon.utils

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
