package com.tankobon.api.route

import com.tankobon.api.models.MangaFilterPayloadModel
import com.tankobon.api.models.MangaTitleUpdatePayloadModel
import com.tankobon.api.models.MangaVolumeUpdatePayloadModel
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.MangaServiceProvider
import com.tankobon.utils.callToFile
import com.tankobon.utils.isAdmin
import com.tankobon.utils.uuidFromString
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.mangaRoute() {
    val mangaService = MangaServiceProvider.get()

    authenticate("auth-jwt") {
        // gets list of all manga without pages
        get("/manga") {
            call.respond(mangaService.getMangaList(null))
        }

        // gets list of manga with filters
        post("/manga") {
            val payload = kotlin.runCatching { call.receiveNullable<MangaFilterPayloadModel>() }.getOrNull()
            call.respond(mangaService.getMangaList(payload))
        }

        // gets detailed info about specific manga title with pages
        get("/manga/{id-title}") {
            call.respond(
                mangaService.getManga(
                    uuidFromString(call.parameters["id-title"])
                )
            )
        }

        // update manga title specific info
        post("/manga/{id-title}/update") {
            isAdmin(call) {
                val payload = call.receive<MangaTitleUpdatePayloadModel>()
                mangaService.updateMangaInfo(call.parameters["id-title"], payload)
                call.respond(HttpStatusCode.OK)
            }
        }

        // update manga volume specific info
        post("/manga/{id-title}/{id-volume}/update") {
            isAdmin(call) {
                val payload = call.receive<MangaVolumeUpdatePayloadModel>()
                mangaService.updateMangaVolumeInfo(call.parameters["id-title"], call.parameters["id-volume"], payload)
                call.respond(HttpStatusCode.OK)
            }
        }

        // gets manga page
        get("/manga/{id-title}/{id-volume}/{id-page}") {
            call.respondFile(callToFile(call, ConfigProvider.get().library.mangaFile))
        }

        // gets manga page thumbnail
        get("/thumb/{id-title}/{id-volume}/{id-page}") {
            call.respondFile(callToFile(call, ConfigProvider.get().library.thumbFile))
        }

        // gets where this user ended reading this title last time
        get("/manga/{id-title}/last-read") {
            TODO("not implemented")
        }

        // sets where this user ended reading this title last time
        post("/manga/{id-title}/last-read") {
            TODO("not implemented")
        }

        // force reload library
        get("/manga/reload-library") {
            isAdmin(call) {
                TODO("not implemented")
            }
        }
    }
}
