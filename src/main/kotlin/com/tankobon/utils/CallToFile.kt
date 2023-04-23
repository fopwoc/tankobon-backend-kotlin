package com.tankobon.utils

import com.tankobon.api.BadRequestError
import com.tankobon.domain.models.MangaRouteType
import io.ktor.server.application.ApplicationCall
import java.io.File

fun callToFile(call: ApplicationCall, initialPath: File): File {
    val idTitle = call.parameters["${MangaRouteType.ID_TITLE}"]
    val idVolume = call.parameters["${MangaRouteType.ID_VOLUME}"]
    val idPage = call.parameters["${MangaRouteType.ID_PAGE}"]

    if (isValidUUID(idTitle) && isValidUUID(idVolume) && isValidUUID(idPage)) {
        return File(
            "${initialPath.path}/$idTitle/$idVolume/$idPage.jpg"
        )
    } else {
        throw BadRequestError()
    }
}
