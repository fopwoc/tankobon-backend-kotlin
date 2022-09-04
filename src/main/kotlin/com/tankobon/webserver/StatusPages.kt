package com.tankobon.webserver

import com.tankobon.webserver.model.ExceptionResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun StatusPagesConfig.statusPages() {
    exception<InternalServerError> { call, trace ->
            call.respond(
            HttpStatusCode.InternalServerError,
            ExceptionResponse(
                type = "unknown",
                message = "${trace.message}",
            ),
        )
    }

    exception<BadRequestError> { call, _ ->
        call.respond(HttpStatusCode.BadRequest)
    }

    exception<AuthenticationException> { call, _ ->
        call.respond(
            HttpStatusCode.Unauthorized,
            ExceptionResponse(
                type = "unauthorized",
                message = null,
            ),
        )
    }

    exception<AdminAuthenticationException> { call, _ ->
        call.respond(
            HttpStatusCode.Unauthorized,
            ExceptionResponse(
                type = "not_admin",
                message = null,
                //message = "no admin privileges",
            ),
        )
    }

    exception<CredentialsException> { call, _ ->
        call.respond(
            HttpStatusCode.Unauthorized,
            ExceptionResponse(
                type = "wrong_credentials",
                message = null,
                //message = "no admin privileges",
            ),
        )
    }

    exception<TokenInvalidException> { call, _ ->
        call.respond(
            HttpStatusCode.Unauthorized,
            ExceptionResponse(
                type = "token_invalid",
                message = null,
                //message = "no admin privileges",
            ),
        )
    }

    exception<UserExistException> { call, _ ->
        call.respond(
            HttpStatusCode.Conflict,
            ExceptionResponse(
                type = "user_exist",
                message = null,
                //message = "this username already exist",
            ),
        )
    }

    exception<NotFoundException> { call, _ ->
        call.respond(HttpStatusCode.NotFound)
    }

}
