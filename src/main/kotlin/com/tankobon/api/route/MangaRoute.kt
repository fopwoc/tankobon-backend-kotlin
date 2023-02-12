package com.tankobon.api.route

import com.tankobon.api.AdminAuthenticationException
import com.tankobon.api.models.MangaIdPayload
import com.tankobon.api.models.MangaPayload
import com.tankobon.api.models.MangaUpdatePayload
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.MangaServiceProvider
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.callToFile
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
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

        post("/manga/{uuid}/update") {
            val requestUser = userService.getUser(
                call.principal<JWTPrincipal>()?.payload?.getClaim("uuid").toString()
            )
            if (requestUser.admin) {
                val payload = call.receive<MangaUpdatePayload>()
                mangaService.updateManga(call.parameters["uuid"], payload)
                call.respond(HttpStatusCode.OK)
            } else {
                throw AdminAuthenticationException()
            }
        }

        get("/manga/{uuid}/{volume}/{page}") {
            call.respondFile(callToFile(call, ConfigProvider.get().library.mangaFile))
        }

        get("/thumb/{uuid}/{volume}/{page}") {
            call.respondFile(callToFile(call, ConfigProvider.get().library.thumbFile))
        }
    }
}
