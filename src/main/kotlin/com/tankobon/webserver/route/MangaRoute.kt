package com.tankobon.webserver.route

import com.tankobon.database.model.MangaIdPayload
import com.tankobon.database.model.MangaPayload
import com.tankobon.database.model.MangaUpdatePayload
import com.tankobon.database.service.MangaService
import com.tankobon.database.service.UserService
import com.tankobon.globalMangaPath
import com.tankobon.globalThumbPath
import com.tankobon.utils.callToFile
import com.tankobon.webserver.AdminAuthenticationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveOrNull
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.mangaRoute(mangaService: MangaService, userService: UserService) {
    authenticate("auth-jwt") {
        get("/manga") {
            call.respond(mangaService.getMangaList(null))
        }

        post("/manga") {
            val payload = call.receiveOrNull<MangaPayload>()
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
            call.respondFile(callToFile(call, globalMangaPath))
        }

        get("/thumb/{uuid}/{volume}/{page}") {
            call.respondFile(callToFile(call, globalThumbPath))
        }
    }
}
