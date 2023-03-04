package com.tankobon.utils

import com.tankobon.domain.providers.ConfigProvider
import java.io.File
import java.util.UUID

fun formatFile(file: File, increaseHierarchy: Boolean): File {
    return File(
        "${file.parent}/${file.nameWithoutExtension}${if (increaseHierarchy) {
            "/" + UUID.randomUUID()
        } else {
            ""
        }}"
    )
}
