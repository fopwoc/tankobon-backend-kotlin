package com.tankobon.api.route

import com.tankobon.api.models.InstanceAboutUpdatePayloadModel
import com.tankobon.domain.providers.InstanceServiceProvider
import com.tankobon.utils.isAdmin
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.instanceRoute() {
    val instanceService = InstanceServiceProvider.get()

    // gets info about this instance
    get("/about") {
        call.respond(instanceService.getAbout())
    }

    authenticate("auth-jwt") {
        // sets info about instance
        post("/about/update") {
            isAdmin(call) {
                instanceService.setAbout(call.receive<InstanceAboutUpdatePayloadModel>())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
