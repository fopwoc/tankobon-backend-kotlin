package com.tankobon.utils

import com.tankobon.api.AdminAuthenticationException
import com.tankobon.api.BadRequestError
import com.tankobon.api.models.UserModel
import com.tankobon.domain.models.MangaParameterType
import com.tankobon.domain.providers.UserServiceProvider
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import java.io.File
import java.util.UUID

suspend inline fun ApplicationCall.isAdmin(): Boolean {
    val user = this.toUser()
    return user.admin
}

suspend inline fun <T> ApplicationCall.isAdmin(function: () -> T) {
    if (this.isAdmin()) {
        function()
    } else {
        throw AdminAuthenticationException()
    }
}

suspend inline fun <reified T : Any> ApplicationCall.receivePayload(function: (payload: T) -> Unit) {
    try {
        function(this.receive<T>())
    } catch (e: CannotTransformContentToTypeException) {
        throw BadRequestError()
    } catch (e: Exception) {
        throw e
    }
}

fun ApplicationCall.toTokenId(): UUID {
    return toTokenId(this.principal<JWTPrincipal>()?.payload ?: throw NotFoundException())
}

suspend fun ApplicationCall.toUser(): UserModel {
    val userService = UserServiceProvider.get()
    return userService.getUser(this.toUserId())
}

fun ApplicationCall.toUserId(): UUID {
    return toUserId(this.principal<JWTPrincipal>()?.payload ?: throw NotFoundException())
}

fun paramToUuid(call: ApplicationCall, paramType: MangaParameterType): UUID {
    val id = uuidFromString(call.parameters["$paramType"])

    if (id != null) {
        return id
    } else {
        throw BadRequestError()
    }
}

fun ApplicationCall.toContentFile(initialPath: File): File {
    val idTitle = paramToUuid(this, MangaParameterType.ID_TITLE)
    val idVolume = paramToUuid(this, MangaParameterType.ID_VOLUME)
    val idPage = paramToUuid(this, MangaParameterType.ID_PAGE)

    return File("${initialPath.path}/$idTitle/$idVolume/$idPage.jpg")
}
