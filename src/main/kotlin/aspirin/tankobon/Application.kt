package aspirin.tankobon

import aspirin.tankobon.database.DatabaseFactory
import aspirin.tankobon.database.service.MangaService
import aspirin.tankobon.database.service.UserService
import aspirin.tankobon.database.service.UtilService
import aspirin.tankobon.manga.filesystem.MangaWatcher
import aspirin.tankobon.webserver.configureErrorStatusPages
import aspirin.tankobon.webserver.configureSecurity
import aspirin.tankobon.webserver.route.authRoute
import aspirin.tankobon.webserver.route.mangaRoute
import aspirin.tankobon.webserver.route.userRoute
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File

val globalAddress = System.getenv("tkbn_address") ?: "0.0.0.0"
val globalPort = Integer.parseInt(System.getenv("tkbn_port") ?: "8080")

val globalIssuer = "http://${globalAddress}:${globalPort}/"

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
            authRoute(userService, utilService)
            userRoute(userService)
            mangaRoute(mangaService)
        }

    }.start(wait = true)
}
