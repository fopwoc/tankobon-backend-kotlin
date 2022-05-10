package aspirin.tankobon.webserver.route

import aspirin.tankobon.database.model.UserAuth
import aspirin.tankobon.database.service.UserService
import aspirin.tankobon.database.service.UtilService
import aspirin.tankobon.globalIssuer
import aspirin.tankobon.webserver.AuthenticationException
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(userService: UserService, utilService: UtilService) {

    post("/login") {
        val user = call.receive<UserAuth>()
        val uuid = userService.authUser(user.username, user.password)
        if (uuid.isNotEmpty()) {
            val token = JWT.create()
                .withIssuer(globalIssuer)
                .withClaim("uuid", uuid)
                .sign(Algorithm.HMAC256(utilService.getSecret()))
            call.respond(hashMapOf("token" to token))
        } else throw AuthenticationException()
    }

}

