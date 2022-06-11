package aspirin.tankobon.webserver

import aspirin.tankobon.database.service.UtilService
import aspirin.tankobon.globalIssuer
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity(utilService: UtilService) {
    authentication {
        jwt("auth-jwt") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(utilService.getSecret()))
                    .withIssuer(globalIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("uuid").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
