package com.tankobon.api.route

import com.tankobon.api.AdminAuthenticationException
import com.tankobon.api.models.CreateUserPayload
import com.tankobon.domain.providers.UserServiceProvider
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

fun Route.userRoute() {
    val userService = UserServiceProvider.get()

    authenticate("auth-jwt") {
        get("/users/me") {
            val principal = call.principal<JWTPrincipal>() // TODO \"\" fix
            call.respond(userService.getUser(principal?.payload?.getClaim("uuid").toString()))
        }

        post("/users/create") {
            val newUser = call.receive<CreateUserPayload>()
            val requestUser = userService.getUser(
                call.principal<JWTPrincipal>()?.payload?.getClaim("uuid").toString()
            )
            if (requestUser.admin) {
                userService.addUser(newUser.username, newUser.password, newUser.admin)
                call.respond(HttpStatusCode.OK, "user ${newUser.username} created")
            } else {
                throw AdminAuthenticationException()
            }
        }
    }
}
