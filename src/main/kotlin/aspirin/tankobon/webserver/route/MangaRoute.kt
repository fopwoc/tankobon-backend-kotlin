package aspirin.tankobon.webserver.route

import aspirin.tankobon.database.service.MangaService
import aspirin.tankobon.globalMangaPath
import aspirin.tankobon.globalThumbPath
import aspirin.tankobon.utils.callToFile
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mangaRoute(mangaService: MangaService) {
    authenticate("auth-jwt") {

        get("/list") {
            call.respond(mangaService.getMangaList())
        }

        get("/manga/{uuid}/{volume}/{page}") {
            call.respondFile(callToFile(call, globalMangaPath))
        }

        get("/thumb/{uuid}/{volume}/{page}") {
            call.respondFile(callToFile(call, globalThumbPath))
        }

    }
}

