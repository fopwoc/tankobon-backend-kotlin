package aspirin.tankobon.webserver

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun StatusPagesConfig.configureErrorStatusPages() {

    exception<InternalServerError> { call, _ ->
        call.respond(HttpStatusCode.InternalServerError)
    }

    exception<BadRequestError> { call, _ ->
        call.respond(HttpStatusCode.BadRequest)
    }

    exception<AuthenticationException> { call, _ ->
        call.respond(HttpStatusCode.Unauthorized)
    }

    exception<AdminAuthenticationException> { call, _ ->
        call.respond(HttpStatusCode.Unauthorized, "no admin privileges")
    }

    exception<UserExistException> { call, _ ->
        call.respond(HttpStatusCode.Conflict, "this username already exist")
    }
}
