package moe.aspirin.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import moe.aspirin.UsersDB
import moe.aspirin.UtilDB

@Serializable
data class User(val username: String, val password: String)

fun Application.configureSecurity() {

    val issuer = "http://0.0.0.0:8080/"

    authentication {
        jwt("auth-jwt") {
            verifier(
                JWT
                .require(Algorithm.HMAC256(UtilDB().utilGetSecret()))
                .withIssuer(issuer)
                .build())
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    routing {
        post("/login") {
            val user = call.receive<User>()
            if (UsersDB().authUser(user.username, user.password)) {
                val token = JWT.create()
                    .withIssuer(issuer)
                    .withClaim("username", user.username)
                    //.withExpiresAt(Date(System.currentTimeMillis() + 60000))
                    .sign(Algorithm.HMAC256(UtilDB().utilGetSecret()))
                call.respond(hashMapOf("token" to token))
            }
            else throw AuthenticationException()
        }
    }
}
