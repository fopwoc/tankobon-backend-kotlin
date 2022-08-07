package com.tankobon.webserver.route

import com.tankobon.database.model.RefreshTokenPayload
import com.tankobon.database.model.UserPayload
import com.tankobon.database.service.TokenService
import com.tankobon.database.service.UserService
import com.tankobon.database.service.UtilsService
import com.tankobon.utils.isValidUUID
import com.tankobon.webserver.AuthenticationException
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoute(userService: UserService, utilsService: UtilsService, tokenService: TokenService) {
    post("/login") {
        val user = call.receive<UserPayload>()
        val uuid = userService.authUser(user.username, user.password)

        if (uuid.isNotEmpty()) {
            val token = tokenService.getTokenPair(uuid, utilsService.getPrivateKey())
            call.respond(token)
        } else {
            throw AuthenticationException()
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
            throw AuthenticationException()
        }
    }
}
