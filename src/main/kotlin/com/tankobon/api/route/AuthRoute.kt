package com.tankobon.api.route

import com.tankobon.api.CredentialsException
import com.tankobon.api.models.RefreshTokenPayloadModel
import com.tankobon.api.models.TokenIdPayloadModel
import com.tankobon.api.models.UserLoginPayloadModel
import com.tankobon.domain.models.AuthRoute
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.TokenServiceProvider
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.toTokenId
import com.tankobon.utils.toUserId
import com.tankobon.utils.isAdmin
import com.tankobon.utils.receivePayload
import com.tankobon.utils.toUser
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
        call.receivePayload<UserLoginPayloadModel>() {
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
        // gets all sessions of current user
        get(AuthRoute.SESSIONS.path) {
            val user = call.toUser()
            call.respond(tokenService.getUserTokens(user))
        }

        // get all sessions of instance
        get(AuthRoute.SESSIONS_ALL.path) {
            call.isAdmin {
                call.respond(tokenService.getAllTokens())
            }
        }

        // delete specific token
        post(AuthRoute.DELETE.path) {
            // admin can delete any session
            if (call.isAdmin()) {
                call.receivePayload<TokenIdPayloadModel> {
                    tokenService.deleteToken(it.id)
                    call.respond(HttpStatusCode.OK)
                }
            } else {
                call.receivePayload<TokenIdPayloadModel> {
                    val userId = call.toUserId()
                    tokenService.deleteToken(it.id, userId)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        // delete all sessions except current one
        get(AuthRoute.CLEANUP.path) {
            val tokenId = call.toTokenId()
            val userId = call.toUserId()
            tokenService.deleteAllTokensExceptThis(tokenId, userId)
            call.respond(HttpStatusCode.OK)
        }

        // logout - delete current session
        get(AuthRoute.LOGOUT.path) {
            val tokenId = call.toTokenId()
            val userId = call.toUserId()
            tokenService.deleteToken(tokenId, userId)
            call.respond(HttpStatusCode.OK)
        }
    }
}
