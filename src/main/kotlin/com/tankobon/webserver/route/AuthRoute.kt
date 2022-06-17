package com.tankobon.webserver.route

import com.tankobon.database.model.RefreshToken
import com.tankobon.database.model.UserAuth
import com.tankobon.database.service.TokenService
import com.tankobon.database.service.UserService
import com.tankobon.database.service.UtilService
import com.tankobon.utils.isValidUUID
import com.tankobon.webserver.AuthenticationException
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoute(userService: UserService, utilService: UtilService, tokenService: TokenService) {

    post("/login") {
        val user = call.receive<UserAuth>()
        val uuid = userService.authUser(user.username, user.password)

        if (uuid.isNotEmpty()) {
            val token = tokenService.getTokenPair(uuid, utilService.getPrivateKey())
            call.respond(token)
        } else {
            throw AuthenticationException()
        }
    }

    post("/refresh") {
        val currentRefreshToken = call.receive<RefreshToken>().refreshToken
        val currentTime = System.currentTimeMillis()
        val tokenData = tokenService.getRefreshData(currentRefreshToken)

        if (tokenData.expires > currentTime && isValidUUID(tokenData.uuid)) {
            val token = tokenService.getTokenPair(tokenData.uuid, utilService.getPrivateKey(), tokenData.refreshToken)
            call.respond(token)
        } else {
            tokenService.deleteRefreshData(currentRefreshToken)
            throw AuthenticationException()
        }
    }
}
