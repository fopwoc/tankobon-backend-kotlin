package com.tankobon

import com.tankobon.database.service.MangaService
import com.tankobon.database.service.TokenService
import com.tankobon.database.service.UserService
import com.tankobon.database.service.UtilsService
import com.tankobon.manga.library.Library
import com.tankobon.utils.logger
import com.tankobon.webserver.route.authRoute
import com.tankobon.webserver.route.mangaRoute
import com.tankobon.webserver.route.userRoute
import com.tankobon.webserver.route.utilsRoute
import com.tankobon.webserver.security
import com.tankobon.webserver.statusPages
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.Routing
import java.io.File
import kotlinx.coroutines.DelicateCoroutinesApi
import org.slf4j.event.Level

val globalAddress = System.getenv("tkbn_address") ?: "localhost"
val globalPort = Integer.parseInt(System.getenv("tkbn_port") ?: "8080")

val globalMangaPath = File(System.getenv("tkbn_manga_path") ?: "manga")
val globalDataPath = File(System.getenv("tkbn_thumb_path") ?: "data")
val globalThumbPath = File("$globalDataPath/thumb")
val globalUnsupportedPath = File("$globalDataPath/unsupported")

val globalInstanceName = System.getenv("tkbn_instance_name") ?: "Tankobon"
val globalInstanceDescription = System.getenv("tkbn_instance_description") ?: "Tankobon instance with some cool manga"

val globalIssuer = "http://$globalAddress:$globalPort/"

val userService = UserService()
val mangaService = MangaService()
val utilsService = UtilsService()
val tokenService = TokenService()

@DelicateCoroutinesApi
fun main() {
    val log = logger("main")
    log.info("Tankōbon-server is starting")

    Library().watchLibrary()
    log.info("Library is ready! webserver has started!")

    embeddedServer(
        Netty,
        host = globalAddress,
        port = globalPort,
        module = Application::webServer
    ).start(wait = true)
}

fun Application.webServer() {
    security(utilsService)

    install(CallLogging) {
        level = Level.INFO
    }

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        statusPages()
    }

    install(Routing) {
        authRoute(userService, utilsService, tokenService)
        userRoute(userService)
        mangaRoute(mangaService, userService)
        utilsRoute(utilsService, userService)
    }
}
