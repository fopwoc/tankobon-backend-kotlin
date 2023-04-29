package com.tankobon.api.route

import com.tankobon.api.CredentialsException
import com.tankobon.api.models.RefreshTokenPayloadModel
import com.tankobon.api.models.TokenIdPayloadModel
import com.tankobon.api.models.UserLoginPayloadModel
import com.tankobon.domain.models.AuthRoute
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.TokenServiceProvider
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.callToTokenId
import com.tankobon.utils.callToUserId
import com.tankobon.utils.isAdmin
import com.tankobon.utils.receivePayload
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.userAgent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Route.authRoute() {
    val userService = UserServiceProvider.get()
    val tokenService = TokenServiceProvider.get()

    // login
    post(AuthRoute.LOGIN.path) {
        receivePayload<UserLoginPayloadModel>(call) {
            val userId = userService.authUser(it.username, it.password)
            val token = tokenService.getTokenPair(
                userId,
                userAgent = call.request.userAgent(),
                // TODO: idk is it correct. And also for second `getTokenPair` usage
                userIP = call.request.local.remoteAddress,
            )
            call.respond(token)
        }
    }

    // token refresh
    post(AuthRoute.REFRESH.path) {
        val currentRefreshToken = call.receive<RefreshTokenPayloadModel>().refreshToken
        val time = Clock.System.now()
        val expireRefresh = ConfigProvider.get().api.expire.refresh
        val tokenData = tokenService.getRefreshData(currentRefreshToken)

        if (expireRefresh == 0 || tokenData.modified.plus(expireRefresh.toDuration(DurationUnit.MILLISECONDS)) > time) {
            val token = tokenService.getTokenPair(
                userId = tokenData.userId,
                userAgent = call.request.userAgent(),
                userIP = call.request.local.remoteAddress,
                oldToken = tokenData.refreshToken,
            )
            call.respond(token)
        } else {
            throw CredentialsException()
        }
    }

    authenticate("auth-jwt") {
        // gets all refresh tokens
        get(AuthRoute.REFRESH.path) {
            val user = userService.callToUser(call)
            call.respond(tokenService.getUserTokens(user))
        }

        // delete specific token
        post(AuthRoute.DELETE.path) {
            receivePayload<TokenIdPayloadModel>(call) {
                val user = callToUserId(call)
                tokenService.deleteTokens(it.id, user)
                call.respond(HttpStatusCode.OK)
            }
        }

        // force cleanup expired tokenss
        get(AuthRoute.CLEANUP.path) {
            isAdmin(call) {
                tokenService.cleanupRefreshTokens()
                call.respond(HttpStatusCode.OK)
            }
        }

        // possible post for logout
        // maybe it should delete all refresh tokens
        get(AuthRoute.LOGOUT.path) {
            val tokenId = callToTokenId(call)
            val user = callToUserId(call)
            tokenService.deleteTokens(tokenId, user)
            call.respond(HttpStatusCode.OK)
        }
    }
}
