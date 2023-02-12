package com.tankobon.domain.database.models

import com.tankobon.api.models.UtilsAbout
import com.tankobon.domain.models.Utils
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

private const val UTILS_MODEL_INSTANCE_UUID_LENGTH = 36
private const val UTILS_MODEL_PUBLIC_KEY_LENGTH = 1024
private const val UTILS_MODEL_PRIVATE_KEY_LENGTH = 4096

object UtilsModel : Table(name = "UTILS") {
    val instanceUuid = varchar("uuid", UTILS_MODEL_INSTANCE_UUID_LENGTH)
    val publicKey = varchar("publickey", UTILS_MODEL_PUBLIC_KEY_LENGTH)
    val privateKey = varchar("privatekey", UTILS_MODEL_PRIVATE_KEY_LENGTH)
    var creationDate = long("creationDate")
    var instanceTitle = text("instanceTitle")
    var instanceDescription = text("instanceDescription")
}

fun ResultRow.toUtils() = Utils(
    instanceUuid = this[UtilsModel.instanceUuid],
    public = this[UtilsModel.publicKey],
    private = this[UtilsModel.privateKey],
    creationDate = this[UtilsModel.creationDate],
)

fun ResultRow.toAbout() = UtilsAbout(
    instanceName = this[UtilsModel.instanceTitle],
    instanceDescription = this[UtilsModel.instanceDescription],
    creationDate = this[UtilsModel.creationDate],
)
