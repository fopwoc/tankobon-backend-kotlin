package com.tankobon.utils

import com.tankobon.api.AdminAuthenticationException
import com.tankobon.domain.providers.UserServiceProvider
import io.ktor.server.application.ApplicationCall

suspend inline fun isAdmin(call: ApplicationCall): Boolean {
    val userService = UserServiceProvider.get()
    val user = userService.callToUser(call)

    return user.admin
}

suspend inline fun <T> isAdmin(call: ApplicationCall, function: () -> T) {
    if (isAdmin(call)) {
        function()
    } else {
        throw AdminAuthenticationException()
    }
}
