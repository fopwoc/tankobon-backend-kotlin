package com.tankobon.api.route

import com.tankobon.api.models.MangaFilterPayloadModel
import com.tankobon.api.models.MangaTitleUpdatePayloadModel
import com.tankobon.api.models.MangaVolumeUpdatePayloadModel
import com.tankobon.domain.models.MangaRoute
import com.tankobon.domain.models.MangaParameterType
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.MangaServiceProvider
import com.tankobon.utils.toContentFile
import com.tankobon.utils.isAdmin
import com.tankobon.utils.paramToUuid
import com.tankobon.utils.receivePayload
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
            call.receivePayload<MangaFilterPayloadModel>() {
                call.respond(mangaService.getMangaList(it))
            }
        }

        // gets detailed info about specific manga title with pages
        get(MangaRoute.MANGA_TITLE.path) {
            call.respond(
                mangaService.getManga(
                    paramToUuid(call, MangaParameterType.ID_TITLE)
                )
            )
        }

        // update manga title specific info
        post(MangaRoute.MANGA_TITLE_UPDATE.path) {
            call.isAdmin {
                call.receivePayload<MangaTitleUpdatePayloadModel>() {
                    mangaService.updateMangaInfo(paramToUuid(call, MangaParameterType.ID_TITLE), it)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        // update manga volume specific info
        post(MangaRoute.MANGA_VOLUME_UPDATE.path) {
            call.isAdmin {
                call.receivePayload<MangaVolumeUpdatePayloadModel>() {
                    mangaService.updateMangaVolumeInfo(
                        paramToUuid(call, MangaParameterType.ID_TITLE),
                        paramToUuid(call, MangaParameterType.ID_VOLUME),
                        it
                    )
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        // gets manga page
        get(MangaRoute.MANGA_PAGE.path) {
            call.respondFile(call.toContentFile(ConfigProvider.get().library.contentFile))
        }

        // gets manga page thumbnail
        get(MangaRoute.THUMB_PAGE.path) {
            call.respondFile(call.toContentFile(ConfigProvider.get().library.thumbFile))
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
            call.isAdmin {
                TODO("not implemented")
            }
        }
    }
}
