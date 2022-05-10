package aspirin.tankobon.webserver

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun StatusPagesConfig.configureErrorStatusPages() {

    exception<InternalServerError> { call, cause ->
        call.respond(HttpStatusCode.InternalServerError)
    }

    exception<BadRequestError> { call, cause ->
        call.respond(HttpStatusCode.BadRequest)
    }

    exception<AuthenticationException> { call, cause ->
        call.respond(HttpStatusCode.Unauthorized)
    }

    exception<AdminAuthenticationException> { call, cause ->
        call.respond(HttpStatusCode.Unauthorized, "no admin privileges")
    }

    exception<UserExistException> { call, cause ->
        call.respond(HttpStatusCode.Conflict, "this username already exist")
    }

}