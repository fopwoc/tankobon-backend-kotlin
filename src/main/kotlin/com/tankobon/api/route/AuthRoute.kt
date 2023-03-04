package com.tankobon.api.route

import com.tankobon.api.CredentialsException
import com.tankobon.api.models.RefreshTokenPayload
import com.tankobon.api.models.UserPayload
import com.tankobon.domain.providers.TokenServiceProvider
import com.tankobon.domain.providers.UserServiceProvider
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.userAgent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoute() {
    val userService = UserServiceProvider.get()
    val tokenService = TokenServiceProvider.get()

    post("/login") {
        val user = call.receive<UserPayload>()
        val userId = userService.authUser(user.username, user.password)
        val token = tokenService.getTokenPair(userId, userAgent = call.request.userAgent())
        call.respond(token)
    }

    post("/refresh") {
        val currentRefreshToken = call.receive<RefreshTokenPayload>().refreshToken
        val currentTime = System.currentTimeMillis()
        val tokenData = tokenService.getRefreshData(currentRefreshToken)

        if (tokenData.expires > currentTime) {
            val token = tokenService.getTokenPair(tokenData.userId, tokenData.refreshToken)
            call.respond(token)
        } else {
            tokenService.deleteRefreshData(currentRefreshToken)
            throw CredentialsException()
        }
    }
}
