package com.tankobon.webserver.route

import com.tankobon.database.model.UtilsAboutUpdatePayload
import com.tankobon.database.service.UserService
import com.tankobon.database.service.UtilsService
import com.tankobon.webserver.AdminAuthenticationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.utilsRoute(utilsService: UtilsService, userService: UserService) {

    get("/about") {
        call.respond(utilsService.getAbout())
    }

    authenticate("auth-jwt") {
        post("/about/update") {
            val payload = call.receive<UtilsAboutUpdatePayload>()
            val requestUser = userService.getUser(
                call.principal<JWTPrincipal>()?.payload?.getClaim("uuid").toString()
            )
            if (requestUser.admin) {
                utilsService.setAbout(payload)
                call.respond(HttpStatusCode.OK)
            } else {
                throw AdminAuthenticationException()
            }
        }
    }
}
