package com.tankobon.utils

import com.tankobon.domain.providers.ConfigProvider
import java.io.File

fun formatFile(file: File, increaseHierarchy: Boolean): File {
    return File(
        "${file.parent}/${file.nameWithoutExtension}${if (increaseHierarchy) {
            "/" +
                formatDigits(0, ConfigProvider.get().library.titleDigits)
        } else {
            ""
        }}"
    )
}
