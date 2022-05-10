package aspirin.tankobon.webserver.route

import aspirin.tankobon.database.model.UserNew
import aspirin.tankobon.database.service.UserService
import aspirin.tankobon.webserver.AdminAuthenticationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute(userService: UserService) {
    authenticate("auth-jwt") {

        get("/me") {
            val principal = call.principal<JWTPrincipal>()
            call.respond(userService.getUser(principal!!.payload.getClaim("uuid").toString()))
        }

        post("/newuser") {
            val newUser = call.receive<UserNew>()
            val requestUser = userService.getUser(
                call.principal<JWTPrincipal>()!!.payload.getClaim("uuid").toString()
            )
            if (requestUser.admin) {
                userService.addUser(newUser.username, newUser.password, newUser.admin)
                call.respond(HttpStatusCode.OK, "user ${newUser.username} created")
            } else {
                throw AdminAuthenticationException()
            }
        }

    }
}