package com.tankobon.webserver.route

import com.tankobon.database.model.CreateUserPayload
import com.tankobon.database.service.UserService
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

fun Route.userRoute(userService: UserService) {
    authenticate("auth-jwt") {
        get("/me") {
            val principal = call.principal<JWTPrincipal>()
            call.respond(userService.getUser(principal!!.payload.getClaim("uuid").toString()))
        }

        post("/newuser") {
            val newUser = call.receive<CreateUserPayload>()
            val requestUser = userService.getUser(
                call.principal<JWTPrincipal>()!!.payload.getClaim("uuid").toString()
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
