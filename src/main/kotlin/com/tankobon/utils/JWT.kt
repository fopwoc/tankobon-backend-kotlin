package com.tankobon.utils

import com.auth0.jwt.interfaces.Payload
import com.tankobon.domain.models.TokenClaim
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.plugins.NotFoundException
import java.util.UUID

fun JWTCredential.toTokenId(): UUID {
    return toTokenId(this.payload)
}

internal fun toTokenId(payload: Payload): UUID {
    return uuidFromString(
        payload.getClaim(TokenClaim.TOKEN_ID.name)?.asString()
    ) ?: throw NotFoundException()
}

fun JWTCredential.toUserId(): UUID {
    return toUserId(this.payload)
}

internal fun toUserId(payload: Payload): UUID {
    return uuidFromString(
        payload.getClaim(TokenClaim.USER_ID.name)?.asString()
    ) ?: throw NotFoundException()
}
