package com.tankobon.utils

import com.tankobon.api.BadRequestError
import com.tankobon.domain.providers.ConfigProvider
import io.ktor.server.application.ApplicationCall
import java.io.File

fun callToFile(call: ApplicationCall, initialPath: File): File {
    val uuid = call.parameters["uuid"]
    val volume = call.parameters["volume"]
    val page = call.parameters["page"]

    if (isValidUUID(uuid) && isNumber(volume.orEmpty()) && isNumber(page.orEmpty())) {
        return File(
            "${initialPath.path}/$uuid/" +
                "${formatDigits(volume?.toIntOrNull() ?: 0, ConfigProvider.get().library.titleDigits)}/" +
                "${formatDigits(page?.toIntOrNull() ?: 0, ConfigProvider.get().library.volumeDigits)}.jpg"
        )
    } else {
        throw BadRequestError()
    }
}
