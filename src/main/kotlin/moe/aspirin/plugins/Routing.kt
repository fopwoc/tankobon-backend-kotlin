package moe.aspirin.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.response.*
import io.ktor.routing.*
import moe.aspirin.MangaDB
import java.io.File
import java.util.*

fun Application.configureRouting() {

    routing {

        authenticate("auth-jwt") {
            get("/hello") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
            }

            get("/list") {
                call.respond(MangaDB().getMangaList())
            }

            get("/manga/{uuid}/{volume}/{page}") {
                call.respondFile(
                    File("manga/${MangaDB().getPathByUUID(UUID.fromString(call.parameters["uuid"]))}").listFiles()
                        ?.filter { e -> e.isDirectory }
                        ?.sortedBy { it.name.toString() }
                        ?.get(call.parameters["volume"]!!.toInt())
                        ?.listFiles()
                        ?.filter { e -> e.isFile && !e.name.contains(".DS_Store") }
                        ?.sortedBy { it.name.toString() }
                        ?.get(call.parameters["page"]!!.toInt())!!
                )
            }

            get("/thumb/{uuid}/{volume}/{page}") {
                call.respondFile(
                    File("${File("manga/${MangaDB().getPathByUUID(UUID.fromString(call.parameters["uuid"]))}").listFiles()
                        ?.filter { e -> e.isDirectory }
                        ?.sortedBy { it.name.toString() }
                        ?.get(call.parameters["volume"]!!.toInt())!!.absoluteFile}/thumb")
                        .listFiles()
                        ?.filter { e -> e.isFile && !e.name.contains(".DS_Store") }
                        ?.sortedBy { it.name.toString() }
                        ?.get(call.parameters["page"]!!.toInt())!!
                )
            }
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
