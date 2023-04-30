package com.tankobon.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.InstanceServiceProvider
import com.tankobon.domain.providers.TokenServiceProvider
import com.tankobon.domain.providers.UserServiceProvider
import com.tankobon.utils.toTokenId
import com.tankobon.utils.toUserId
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import kotlinx.coroutines.runBlocking

fun Application.security() {
    val instanceService = InstanceServiceProvider.get()
    val tokenService = TokenServiceProvider.get()
    val userService = UserServiceProvider.get()

    authentication {
        jwt("auth-jwt") {
            val publicKey = runBlocking { instanceService.getPublicKey() }
            verifier(
                JWT.require(Algorithm.RSA256(publicKey, null))
                    .withIssuer(ConfigProvider.get().api.issuer)
                    .build()
            )
            validate { credential ->
                val tokenId = credential.toTokenId()
                val userId = credential.toUserId()

                if (tokenService.checkCredentials(tokenId, userId)) {
                    val isActive = userService.isUserActive(userId)
                    if (!isActive) throw UserDisabledException()

                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                throw AuthenticationException()
            }
        }
    }
}
