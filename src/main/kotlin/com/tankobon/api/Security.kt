package com.tankobon.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.InstanceServiceProvider
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.security() {
    val instanceService = InstanceServiceProvider.get()

    authentication {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.RSA256(instanceService.getPublicKey(), null))
                    .withIssuer(ConfigProvider.get().api.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != "") {
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
