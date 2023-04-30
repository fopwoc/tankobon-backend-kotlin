package com.tankobon.api.route

import com.tankobon.api.models.CreateUserPayloadModel
import com.tankobon.domain.models.UserRoute
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.isAdmin
import com.tankobon.utils.receivePayload
import com.tankobon.utils.toUser
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
            call.respond(call.toUser())
        }

        // get all users
        get(UserRoute.ALL.path) {
            call.isAdmin {
                call.respond(userService.getAllUsers())
            }
        }

        // creates new user
        post(UserRoute.CREATE.path) {
            call.isAdmin {
                call.receivePayload<CreateUserPayloadModel> {
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
            call.isAdmin {
                TODO("not implemented")
            }
        }

        post(UserRoute.DELETE.path) {
            call.isAdmin {
                TODO("not implemented")
            }
        }
    }
}
