package com.tankobon.api.route

import com.tankobon.api.CredentialsException
import com.tankobon.api.models.RefreshTokenPayloadModel
import com.tankobon.api.models.UserLoginPayloadModel
import com.tankobon.domain.providers.TokenServiceProvider
import com.tankobon.domain.providers.UserServiceProvider
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.userAgent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

// TODO: maybe change base url /auth/login for example
fun Route.authRoute() {
    val userService = UserServiceProvider.get()
    val tokenService = TokenServiceProvider.get()

    // login
    post("/login") {
        val user = call.receive<UserLoginPayloadModel>()
        val userId = userService.authUser(user.username, user.password)
        val token = tokenService.getTokenPair(
            userId,
            userAgent = call.request.userAgent(),
            // TODO: idk is it correct. And also for second `getTokenPair` usage
            userIP = call.request.local.remoteAddress,
        )
        call.respond(token)
    }

    // token refresh
    post("/refresh") {
        val currentRefreshToken = call.receive<RefreshTokenPayloadModel>().refreshToken
        val currentTime = System.currentTimeMillis()
        val tokenData = tokenService.getRefreshData(currentRefreshToken)

        if (tokenData.expires > currentTime) {
            val token = tokenService.getTokenPair(
                userId = tokenData.userId,
                userAgent = call.request.userAgent(),
                userIP = call.request.local.remoteAddress,
                oldToken = tokenData.refreshToken,
            )
            call.respond(token)
        } else {
            tokenService.deleteRefreshData(currentRefreshToken)
            throw CredentialsException()
        }
    }

    // gets all refresh tokens
    get("/refresh") {
        TODO("not implementes")
    }

    // possible post for logout
    // maybe it should delete all refresh tokens
    post("/logout") {
        TODO("not implemented")
    }
}
