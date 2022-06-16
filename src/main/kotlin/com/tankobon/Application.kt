package com.tankobon

import com.tankobon.database.DatabaseFactory
import com.tankobon.database.service.MangaService
import com.tankobon.database.service.TokenService
import com.tankobon.database.service.UserService
import com.tankobon.database.service.UtilService
import com.tankobon.manga.filesystem.MangaWatcher
import com.tankobon.webserver.configureErrorStatusPages
import com.tankobon.webserver.configureSecurity
import com.tankobon.webserver.route.authRoute
import com.tankobon.webserver.route.mangaRoute
import com.tankobon.webserver.route.userRoute
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.Routing
import kotlinx.coroutines.DelicateCoroutinesApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File

val globalAddress = System.getenv("tkbn_address") ?: "0.0.0.0"
val globalPort = Integer.parseInt(System.getenv("tkbn_port") ?: "8080")

val globalIssuer = "http://$globalAddress:$globalPort/"

val globalMangaPath = File("manga")
val globalThumbPath = File("data/thumb")

val logger: Logger = LoggerFactory.getLogger("tankobon")

@DelicateCoroutinesApi
fun main() {
    logger.info("Tankōbon-server is starting!")

    val serviceDB = DatabaseFactory.init()

    val userService = UserService(serviceDB)
    val mangaService = MangaService(serviceDB)
    val utilService = UtilService(serviceDB)
    val tokenService = TokenService(serviceDB)

    MangaWatcher(mangaService).watchFolder()
    logger.info("Library is ready! webserver has started!")

    embeddedServer(
        Netty,
        host = globalAddress,
        port = globalPort,
    ) {
        configureSecurity(utilService)

        install(CallLogging) {
            level = Level.INFO
        }

        install(ContentNegotiation) {
            json()
        }

        install(StatusPages) {
            configureErrorStatusPages()
        }

        install(Routing) {
            authRoute(userService, utilService, tokenService)
            userRoute(userService)
            mangaRoute(mangaService)
        }
    }.start(wait = true)
}
