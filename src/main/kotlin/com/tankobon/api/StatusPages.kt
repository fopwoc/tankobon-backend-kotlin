package com.tankobon.api

import com.tankobon.api.models.ExceptionMessageModel
import com.tankobon.domain.models.BackendExceptionType
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
            ExceptionMessageModel(
                type = BackendExceptionType.WRONG_CREDENTIALS,
                message = null,
            ),
        )
    }

    exception<AdminAuthenticationException> { call, _ ->
        call.respond(
            HttpStatusCode.Forbidden,
            ExceptionMessageModel(
                type = BackendExceptionType.NOT_ADMIN,
                message = null,
            ),
        )
    }

    exception<UserExistException> { call, _ ->
        call.respond(
            HttpStatusCode.Conflict,
            ExceptionMessageModel(
                type = BackendExceptionType.USER_EXIST,
                message = null,
            ),
        )
    }

    exception<UserDisabledException> { call, _ ->
        call.respond(
            HttpStatusCode.Locked,
            ExceptionMessageModel(
                type = BackendExceptionType.USER_DISABLED,
                message = null,
            ),
        )
    }

    exception<InternalServerError> { call, trace ->
        call.respond(
            HttpStatusCode.InternalServerError,
            ExceptionMessageModel(
                type = BackendExceptionType.UNKNOWN,
                message = trace.message,
            ),
        )
    }
}
