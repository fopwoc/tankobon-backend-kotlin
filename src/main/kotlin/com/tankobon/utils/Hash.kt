package com.tankobon.utils

import java.io.File
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

fun md5(file: File): String = Base64.getEncoder().encodeToString(
    BigInteger(1, MessageDigest.getInstance("MD5").digest(file.readBytes())).toByteArray()
)

fun sha256(str: String): ByteArray =
    MessageDigest.getInstance("SHA-256")
        .digest(str.toByteArray(StandardCharsets.UTF_8))

fun ByteArray.toHex() =
    joinToString(separator = "") { byte -> "%02x".format(byte) }
