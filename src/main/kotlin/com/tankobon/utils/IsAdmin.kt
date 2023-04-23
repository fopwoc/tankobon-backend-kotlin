package com.tankobon.utils

import com.tankobon.api.AdminAuthenticationException
import com.tankobon.domain.providers.UserServiceProvider
import io.ktor.server.application.ApplicationCall

suspend inline fun <T> isAdmin(call: ApplicationCall, function: () -> T) {
    val userService = UserServiceProvider.get()
    val user = userService.callToUser(call)

    if (user.admin) {
        function()
    } else {
        throw AdminAuthenticationException()
    }
}
