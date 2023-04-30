package com.tankobon.api.route

import com.tankobon.api.models.UserCreatePayloadModel
import com.tankobon.api.models.UserIdPayloadModel
import com.tankobon.api.models.UserUpdatePayloadModel
import com.tankobon.domain.models.UserRoute
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.isAdmin
import com.tankobon.utils.receivePayload
import com.tankobon.utils.toUser
import com.tankobon.utils.toUserId
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
                call.receivePayload<UserCreatePayloadModel> {
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

        // edit user
        post(UserRoute.EDIT.path) {
            call.receivePayload<UserUpdatePayloadModel> {
                val userId = call.toUserId()
                if (userId == it.id) {
                    userService.editUser(it, false)
                } else {
                    call.isAdmin {
                        userService.editUser(it, true)
                    }
                }
                call.respond(HttpStatusCode.OK)
            }
        }

        // toggle user
        post(UserRoute.TOGGLE.path) {
            call.isAdmin {
                call.receivePayload<UserIdPayloadModel> {
                    userService.toggleUser(it.id)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        // delete user
        post(UserRoute.DELETE.path) {
            call.isAdmin {
                call.receivePayload<UserIdPayloadModel> {
                    userService.deleteUser(it.id)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
