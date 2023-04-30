package com.tankobon.api.route

import com.tankobon.api.models.CreateUserPayloadModel
import com.tankobon.domain.models.UserRoute
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.isAdmin
import com.tankobon.utils.receivePayload
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.userRoute() {
    val userService = UserServiceProvider.get()

    authenticate("auth-jwt") {
        // gets info about user itself
        get(UserRoute.ME.path) {
            call.respond(userService.callToUser(call))
        }

        // get all users
        get(UserRoute.ALL.path) {
            isAdmin(call) {
                call.respond(userService.getAllUsers())
            }
        }

        // creates new user
        post(UserRoute.CREATE.path) {
            isAdmin(call) {
                receivePayload<CreateUserPayloadModel>(call) {
                    userService.addUser(
                        username = it.username,
                        password = it.password,
                        isAdmin = it.admin,
                        isActive = it.active ?: true,
                    )
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        post(UserRoute.EDIT.path) {
            isAdmin(call) {
                TODO("not implemented")
            }
        }

        post(UserRoute.DELETE.path) {
            isAdmin(call) {
                TODO("not implemented")
            }
        }
    }
}
