package com.tankobon.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.InstanceServiceProvider
import com.tankobon.domain.providers.TokenServiceProvider
import com.tankobon.utils.callToTokenId
import com.tankobon.utils.callToUserId
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.security() {
    val instanceService = InstanceServiceProvider.get()
    val tokenService = TokenServiceProvider.get()

    authentication {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.RSA256(instanceService.getPublicKey(), null))
                    .withIssuer(ConfigProvider.get().api.issuer)
                    .build()
            )
            validate { credential ->
                val tokenId = callToTokenId(credential)
                val userId = callToUserId(credential)

                if (tokenService.checkCredentials(tokenId, userId)) {
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
