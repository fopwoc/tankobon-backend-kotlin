package com.tankobon.api.route

import com.tankobon.api.AdminAuthenticationException
import com.tankobon.api.models.MangaIdPayload
import com.tankobon.api.models.MangaPayload
import com.tankobon.api.models.MangaUpdatePayload
import com.tankobon.api.models.MangaVolumeUpdatePayload
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.MangaServiceProvider
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.callToFile
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
    val userService = UserServiceProvider.get()

    authenticate("auth-jwt") {
        get("/manga") {
            call.respond(mangaService.getMangaList(null))
        }

        post("/manga") {
            val payload = kotlin.runCatching { call.receiveNullable<MangaPayload>() }.getOrNull()
            call.respond(mangaService.getMangaList(payload))
        }

        post("/manga/id") {
            val payload = call.receive<MangaIdPayload>()
            call.respond(mangaService.getManga(payload.id))
        }

        post("/manga/{id-title}/update") {
            val requestUser = userService.getUserCall(call)

            if (requestUser.admin) {
                val payload = call.receive<MangaUpdatePayload>()
                mangaService.updateMangaInfo(call.parameters["id-title"], payload)
                call.respond(HttpStatusCode.OK)
            } else {
                throw AdminAuthenticationException()
            }
        }

        post("/manga/{id-title}/{id-volume}/update") {
            val requestUser = userService.getUserCall(call)

            if (requestUser.admin) {
                val payload = call.receive<MangaVolumeUpdatePayload>()
                mangaService.updateMangaVolumeInfo(call.parameters["id-title"], call.parameters["id-volume"], payload)
                call.respond(HttpStatusCode.OK)
            } else {
                throw AdminAuthenticationException()
            }
        }

        get("/manga/{id-title}/{id-volume}/{id-page}") {
            call.respondFile(callToFile(call, ConfigProvider.get().library.mangaFile))
        }

        get("/thumb/{id-title}/{id-volume}/{id-page}") {
            call.respondFile(callToFile(call, ConfigProvider.get().library.thumbFile))
        }
    }
}
