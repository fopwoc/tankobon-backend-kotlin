package aspirin.tankobon.utils

import aspirin.tankobon.webserver.BadRequestError
import io.ktor.server.application.ApplicationCall
import java.io.File

fun callToFile(call: ApplicationCall, initialPath: File): File {
    val uuid = call.parameters["uuid"]
    val volume = call.parameters["volume"]
    val page = call.parameters["page"]

    if (isValidUUID(uuid) && Regex("^(\\d*)\$").matches(volume ?: "") && Regex("^(\\d*)\$").matches(page ?: "")) {
        return File("${initialPath.path}/$uuid/$volume/$page.jpg")
    } else {
        throw BadRequestError()
    }
}
