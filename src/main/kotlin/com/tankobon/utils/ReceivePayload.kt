package com.tankobon.utils

import com.auth0.jwt.interfaces.Payload
import com.tankobon.api.BadRequestError
import com.tankobon.domain.models.TokenClaim
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import java.util.UUID

suspend inline fun <reified T : Any> receivePayload(call: ApplicationCall, function: (payload: T) -> Unit) {
    try {
        function(call.receive<T>())
    } catch (e: CannotTransformContentToTypeException) {
        throw BadRequestError()
    } catch (e: Exception) {
        throw e
    }
}

fun callToTokenId(call: ApplicationCall): UUID {
    return callToTokenId(call.principal<JWTPrincipal>()?.payload ?: throw NotFoundException())
}

fun callToTokenId(principal: JWTCredential): UUID {
    return callToTokenId(principal.payload)
}

private fun callToTokenId(payload: Payload): UUID {
    return uuidFromString(
        payload.getClaim(TokenClaim.TOKEN_ID.name)?.asString()
    ) ?: throw NotFoundException()
}

fun callToUserId(call: ApplicationCall): UUID {
    return callToUserId(call.principal<JWTPrincipal>()?.payload ?: throw NotFoundException())
}

fun callToUserId(principal: JWTCredential): UUID {
    return callToUserId(principal.payload)
}

private fun callToUserId(payload: Payload): UUID {
    return uuidFromString(
        payload.getClaim(TokenClaim.USER_ID.name)?.asString()
    ) ?: throw NotFoundException()
}
