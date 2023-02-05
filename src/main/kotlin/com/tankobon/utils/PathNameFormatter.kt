package com.tankobon.utils

import java.io.File

fun pathNameFormatter(file: File, increaseHierarchy: Boolean): File {
    return File("${file.parent}/${file.nameWithoutExtension}${if (increaseHierarchy) "/0000" else ""}")
}
