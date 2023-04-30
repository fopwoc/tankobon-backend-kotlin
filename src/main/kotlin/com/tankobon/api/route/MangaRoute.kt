package com.tankobon.api.route

import com.tankobon.api.models.MangaFilterPayloadModel
import com.tankobon.api.models.MangaTitleUpdatePayloadModel
import com.tankobon.api.models.MangaVolumeUpdatePayloadModel
import com.tankobon.domain.models.MangaRoute
import com.tankobon.domain.models.MangaRouteType
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.MangaServiceProvider
import com.tankobon.utils.callToFile
import com.tankobon.utils.isAdmin
import com.tankobon.utils.receivePayload
import com.tankobon.utils.uuidFromString
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.mangaRoute() {
    val mangaService = MangaServiceProvider.get()

    authenticate("auth-jwt") {
        // gets list of all manga without pages
        get(MangaRoute.MANGA.path) {
            call.respond(mangaService.getMangaList(null))
        }

        // gets list of manga with filters
        post(MangaRoute.MANGA.path) {
            receivePayload<MangaFilterPayloadModel>(call) {
                call.respond(mangaService.getMangaList(it))
            }
        }

        // gets detailed info about specific manga title with pages
        get(MangaRoute.MANGA_TITLE.path) {
            call.respond(
                mangaService.getManga(
                    uuidFromString(call.parameters["${MangaRouteType.ID_TITLE}"])
                )
            )
        }

        // update manga title specific info
        post(MangaRoute.MANGA_TITLE_UPDATE.path) {
            isAdmin(call) {
                receivePayload<MangaTitleUpdatePayloadModel>(call) {
                    mangaService.updateMangaInfo(call.parameters["${MangaRouteType.ID_TITLE}"], it)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        // update manga volume specific info
        post(MangaRoute.MANGA_VOLUME_UPDATE.path) {
            isAdmin(call) {
                receivePayload<MangaVolumeUpdatePayloadModel>(call) {
                    mangaService.updateMangaVolumeInfo(
                        call.parameters["${MangaRouteType.ID_TITLE}"],
                        call.parameters["${MangaRouteType.ID_VOLUME}"],
                        it
                    )
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        // gets manga page
        get(MangaRoute.MANGA_PAGE.path) {
            call.respondFile(callToFile(call, ConfigProvider.get().library.contentFile))
        }

        // gets manga page thumbnail
        get(MangaRoute.THUMB_PAGE.path) {
            call.respondFile(callToFile(call, ConfigProvider.get().library.thumbFile))
        }

        // gets where this user ended reading this title last time
        get(MangaRoute.LAST_READ.path) {
            TODO("not implemented")
        }

        // sets where this user ended reading this title last time
        post(MangaRoute.LAST_READ.path) {
            TODO("not implemented")
        }

        // force reload library
        get(MangaRoute.RELOAD_LIBRARY.path) {
            isAdmin(call) {
                TODO("not implemented")
            }
        }
    }
}
