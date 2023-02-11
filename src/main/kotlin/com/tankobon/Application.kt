package com.tankobon

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceOrFileSource
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
import kotlinx.coroutines.DelicateCoroutinesApi
import org.slf4j.event.Level
import java.io.File

data class Config(
    val globalAddress: String,
    val globalPort: Int,
    val globalMangaPath: String,
    val globalDataPath: String,
    val globalInstanceName: String,
    val globalInstanceDescription: String,
)

val configPath = File(System.getenv("tkbn_config_path") ?: "tankobon-config.yml")

val globalConfig = ConfigLoaderBuilder
    .default()
    .addResourceOrFileSource(
        configPath.absolutePath,
    ).build()
    .loadConfigOrThrow<Config>()

val globalAddress = globalConfig.globalAddress
val globalPort = globalConfig.globalPort

val globalMangaPath = File(globalConfig.globalMangaPath).canonicalFile
val globalDataPath = File(globalConfig.globalDataPath).canonicalFile
val globalThumbPath = File("$globalDataPath/thumb").canonicalFile
val globalUnsupportedPath = File("$globalDataPath/unsupported").canonicalFile

val globalInstanceName = System.getenv("tkbn_instance_name") ?: "Tankobon"
val globalInstanceDescription = System.getenv("tkbn_instance_description") ?: "Tankobon instance with some cool manga"

val globalIssuer = "http://$globalAddress:$globalPort/"

val userService = UserService()
val mangaService = MangaService()
val utilsService = UtilsService()
val tokenService = TokenService()

@DelicateCoroutinesApi
fun main(args: Array<String>) {
    val log = logger("main")
    log.info("Tankōbon-server is starting")

    Library().watchLibrary()
    log.info("Library is ready! webserver has started!")

    embeddedServer(
        Netty,
        host = globalAddress,
        port = globalPort,
        module = Application::webServer,
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
