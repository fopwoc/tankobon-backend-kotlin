package moe.aspirin

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import moe.aspirin.plugins.AuthenticationException
import moe.aspirin.plugins.AuthorizationException
import moe.aspirin.plugins.configureRouting
import moe.aspirin.plugins.configureSecurity
import org.slf4j.event.Level

fun main() {

    UtilDB().init()
    MangaService().watchFolder()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(CallLogging) {
            level = Level.INFO
        }

        install(ContentNegotiation) {
            json()
        }

        install(StatusPages) {
            exception<AuthenticationException> {
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> {
                call.respond(HttpStatusCode.Forbidden)
            }
        }

        configureSecurity()
        configureRouting()

    }.start(wait = true)
}
