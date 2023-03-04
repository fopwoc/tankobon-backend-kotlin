package com.tankobon.utils

import com.tankobon.api.BadRequestError
import io.ktor.server.application.ApplicationCall
import java.io.File

fun callToFile(call: ApplicationCall, initialPath: File): File {
    val idTitle = call.parameters["id-title"]
    val idVolume = call.parameters["id-volume"]
    val idPage = call.parameters["id-page"]

    if (isValidUUID(idTitle) && isValidUUID(idVolume) && isValidUUID(idPage)) {
        return File(
            "${initialPath.path}/$idTitle/$idVolume/$idPage.jpg"
        )
    } else {
        throw BadRequestError()
    }
}
