package com.tankobon.api.route

import com.tankobon.api.models.InstanceAboutUpdatePayloadModel
import com.tankobon.domain.models.InstanceRoute
import com.tankobon.domain.providers.InstanceServiceProvider
import com.tankobon.utils.isAdmin
import com.tankobon.utils.receivePayload
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.instanceRoute() {
    val instanceService = InstanceServiceProvider.get()

    // gets info about this instance
    get(InstanceRoute.ABOUT.path) {
        call.respond(instanceService.getAbout())
    }

    authenticate("auth-jwt") {
        // sets info about instance
        post(InstanceRoute.ABOUT_UPDATE.path) {
            isAdmin(call) {
                receivePayload<InstanceAboutUpdatePayloadModel>(call) {
                    instanceService.setAbout(it)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
