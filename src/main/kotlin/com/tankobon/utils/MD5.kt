package com.tankobon.utils

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Base64

fun md5(file: File): String {
    return Base64.getEncoder().encodeToString(
        BigInteger(1, MessageDigest.getInstance("MD5").digest(file.readBytes())).toByteArray()
    )
}
