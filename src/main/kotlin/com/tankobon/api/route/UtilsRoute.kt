package com.tankobon.api.route

import com.tankobon.api.AdminAuthenticationException
import com.tankobon.api.models.UtilsAboutUpdatePayload
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.domain.providers.UtilsServiceProvider
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

fun Route.utilsRoute() {
    val utilsService = UtilsServiceProvider.get()
    val userService = UserServiceProvider.get()

    get("/about") {
        call.respond(utilsService.getAbout())
    }

    authenticate("auth-jwt") {
        post("/about/update") {
            val payload = call.receive<UtilsAboutUpdatePayload>()
            val requestUser = userService.getUser(
                call.principal<JWTPrincipal>()?.payload?.getClaim("userId").toString()
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
