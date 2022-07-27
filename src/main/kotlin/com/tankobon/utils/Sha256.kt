package com.tankobon.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun sha256(str: String): ByteArray =
    MessageDigest.getInstance("SHA-256")
        .digest(str.toByteArray(StandardCharsets.UTF_8))

fun ByteArray.toHex() =
    joinToString(separator = "") { byte -> "%02x".format(byte) }
