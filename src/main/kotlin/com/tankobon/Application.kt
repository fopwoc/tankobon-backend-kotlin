package com.tankobon

import com.tankobon.api.route.authRoute
import com.tankobon.api.route.mangaRoute
import com.tankobon.api.route.userRoute
import com.tankobon.api.route.utilsRoute
import com.tankobon.api.security
import com.tankobon.api.statusPages
import com.tankobon.domain.library.Library
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.utils.logger
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

@DelicateCoroutinesApi
fun main() {
    val log = logger("main")
    log.info("Tankōbon-server is starting")

    Library().watchLibrary()

    log.info("Library is ready! webserver has started!")
    embeddedServer(
        Netty,
        host = ConfigProvider.get().api.address,
        port = ConfigProvider.get().api.port,
        module = Application::webServer,
    ).start(wait = true)
}

fun Application.webServer() {
    security()

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
        authRoute()
        userRoute()
        mangaRoute()
        utilsRoute()
    }
}
