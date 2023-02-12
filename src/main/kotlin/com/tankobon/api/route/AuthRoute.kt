package com.tankobon.api.route

import com.tankobon.api.CredentialsException
import com.tankobon.api.models.RefreshTokenPayload
import com.tankobon.api.models.UserPayload
import com.tankobon.domain.providers.TokenServiceProvider
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.domain.providers.UtilsServiceProvider
import com.tankobon.utils.isValidUUID
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoute() {
    val userService = UserServiceProvider.get()
    val tokenService = TokenServiceProvider.get()
    val utilsService = UtilsServiceProvider.get()

    post("/login") {
        val user = call.receive<UserPayload>()
        val uuid = userService.authUser(user.username, user.password)

        if (uuid.isNotEmpty()) {
            val token = tokenService.getTokenPair(uuid, utilsService.getPrivateKey())
            call.respond(token)
        } else {
            throw CredentialsException()
        }
    }

    post("/refresh") {
        val currentRefreshToken = call.receive<RefreshTokenPayload>().refreshToken
        val currentTime = System.currentTimeMillis()
        val tokenData = tokenService.getRefreshData(currentRefreshToken)

        if (tokenData.expires > currentTime && isValidUUID(tokenData.uuid)) {
            val token = tokenService.getTokenPair(tokenData.uuid, utilsService.getPrivateKey(), tokenData.refreshToken)
            call.respond(token)
        } else {
            tokenService.deleteRefreshData(currentRefreshToken)
            throw CredentialsException()
        }
    }
}
