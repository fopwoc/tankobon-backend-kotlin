package com.tankobon.api

import com.tankobon.api.models.ExceptionResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun StatusPagesConfig.statusPages() {
    exception<BadRequestError> { call, _ ->
        call.respond(HttpStatusCode.BadRequest)
    }

    exception<AuthenticationException> { call, _ ->
        call.respond(HttpStatusCode.Unauthorized)
    }

    exception<NotFoundException> { call, _ ->
        call.respond(HttpStatusCode.NotFound)
    }

    exception<CredentialsException> { call, _ ->
        call.respond(
            HttpStatusCode.Forbidden,
            ExceptionResponse(
                type = "wrong_credentials",
                message = null,
            ),
        )
    }

    exception<AdminAuthenticationException> { call, _ ->
        call.respond(
            HttpStatusCode.Forbidden,
            ExceptionResponse(
                type = "not_admin",
                message = null,
            ),
        )
    }

    exception<UserExistException> { call, _ ->
        call.respond(
            HttpStatusCode.Conflict,
            ExceptionResponse(
                type = "user_exist",
                message = null,
            ),
        )
    }

    exception<InternalServerError> { call, trace ->
        call.respond(
            HttpStatusCode.InternalServerError,
            ExceptionResponse(
                type = "unknown",
                message = "${trace.message}",
            ),
        )
    }
}
