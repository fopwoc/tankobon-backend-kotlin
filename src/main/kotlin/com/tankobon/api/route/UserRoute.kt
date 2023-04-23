package com.tankobon.api.route

import com.tankobon.api.models.CreateUserPayloadModel
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.isAdmin
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
        // gets info about user itself
        get("/users/me") {
            val principal = call.principal<JWTPrincipal>() // TODO \"\" fix
            call.respond(userService.callToUser(call))
        }

        // creates new user
        post("/users/create") {
            isAdmin(call) {
                val newUser = call.receive<CreateUserPayloadModel>()
                userService.addUser(
                    username = newUser.username,
                    password = newUser.password,
                    isAdmin = newUser.admin,
                )
                call.respond(HttpStatusCode.OK, "user ${newUser.username} created")
            }
        }

        post("/users/edit") {
            isAdmin(call) {
                TODO("not implemented")
            }
        }

        post("/users/delete") {
            isAdmin(call) {
                TODO("not implemented")
            }
        }
    }
}
