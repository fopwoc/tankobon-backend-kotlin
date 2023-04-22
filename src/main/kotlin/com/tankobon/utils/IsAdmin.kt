package com.tankobon.utils

import com.tankobon.api.AdminAuthenticationException
import com.tankobon.domain.providers.UserServiceProvider
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

suspend inline fun <T> isAdmin(call: ApplicationCall, function: () -> T) {
    val userService = UserServiceProvider.get()

    val requestUser = userService.getUser(
        call.principal<JWTPrincipal>()?.payload?.getClaim("userId").toString()
    )
    if (requestUser.admin) {
        function()
    } else {
        throw AdminAuthenticationException()
    }
}
