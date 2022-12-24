package com.tankobon

import com.tankobon.manga.library.Library
import com.tankobon.utils.logger
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.io.File

val globalAddress = System.getenv("tkbn_address") ?: "localhost"
val globalPort = Integer.parseInt(System.getenv("tkbn_port") ?: "8080")

val globalMangaPath = File(System.getenv("tkbn_manga_path") ?: "manga")
val globalThumbPath = File(System.getenv("tkbn_thumb_path") ?: "data/thumb")

val globalInstanceName = System.getenv("tkbn_instance_name") ?: "Tankobon"
val globalInstanceDescription = System.getenv("tkbn_instance_description") ?: "Tankobon instance with some cool manga"

val globalIssuer = "http://$globalAddress:$globalPort/"


@DelicateCoroutinesApi
fun main() {
    val log = logger("main")
    log.info("Tankōbon-server is starting")

    runBlocking { Library().watchLibrary() }

//    Thread.sleep(5_000)
//
//    println("unrar")
//    val rarTime = measureTimeMillis {
//        File("./manga/testrar").mkdir()
//        shellRun("unrar", listOf("-inul", "-o+", "x", "./manga/test.rar", "./manga/testrar"))
//    }
//    println("rar time: ${rarTime/1000}")
//
//
//    println("unzip")
//    val zipTime = measureTimeMillis {
//        File("./manga/testcbz").mkdir()
//        shellRun("7z", listOf("-aoa", "x", "./manga/test.cbz", "-o${"./manga/testcbz"}"))
//    }
//    println("zip time: ${zipTime/1000}")

    Thread.sleep(1000_000)

//    val userService = UserService()
//    val mangaService = MangaService()
//    val utilsService = UtilsService()
//    val tokenService = TokenService()
//
//    MangaWatcher(mangaService).watchFolder()
//    logger.info("Library is ready! webserver has started!")
//
//    embeddedServer(
//        Netty,
//        host = globalAddress,
//        port = globalPort
//    ) {
//        security(utilsService)
//
//        install(CallLogging) {
//            level = Level.INFO
//        }
//
//        install(ContentNegotiation) {
//            json()
//        }
//
//        install(StatusPages) {
//            statusPages()
//        }
//
//        install(Routing) {
//            authRoute(userService, utilsService, tokenService)
//            userRoute(userService)
//            mangaRoute(mangaService, userService)
//            utilsRoute(utilsService, userService)
//        }
//    }.start(wait = true)
}
