package com.tankobon.utils

import com.tankobon.webserver.BadRequestError
import io.ktor.server.application.ApplicationCall
import java.io.File

fun callToFile(call: ApplicationCall, initialPath: File): File {
    val uuid = call.parameters["uuid"]
    val volume = call.parameters["volume"]
    val page = call.parameters["page"]

    if (isValidUUID(uuid) && isNumber(volume.orEmpty()) && isNumber(page.orEmpty())) {
        return File("${initialPath.path}/$uuid/$volume/$page.jpg")
    } else {
        throw BadRequestError()
    }
}

private fun isNumber(string: String): Boolean {
    return Regex("^(\\d*)\$").matches(string)
}
