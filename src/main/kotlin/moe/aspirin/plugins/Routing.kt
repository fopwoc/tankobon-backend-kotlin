package moe.aspirin.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import moe.aspirin.MangaDB
import java.io.File
import java.util.*

@Serializable
data class UserJwt(val username: String)

fun Application.configureRouting() {

    routing {

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                call.respondText(Json.encodeToString(UserJwt(principal!!.payload.getClaim("username").asString())))
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
                    File("${File("data/thumb/${MangaDB().getPathByUUID(UUID.fromString(call.parameters["uuid"]))}").listFiles()
                        ?.filter { e -> e.isDirectory }
                        ?.sortedBy { it.name.toString() }
                        ?.get(call.parameters["volume"]!!.toInt())!!.absoluteFile}")
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
