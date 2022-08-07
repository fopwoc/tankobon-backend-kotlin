package com.tankobon.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object UtilsModel : Table(name = "UTILS") {
    val instanceId = varchar("uuid", 36)
    val public = varchar("public", 512)
    val private = varchar("private", 2048)
    var creationDate = long("creationDate")
    var instanceName = text("instanceName")
    var instanceDescription = text("instanceDescription")
}

data class Utils(
    val instanceId: String,
    val public: String,
    val private: String,
    val creationDate: Long
)

@Serializable
data class UtilsAbout(
    val instanceName: String,
    val instanceDescription: String,
    val creationDate: Long
)

@Serializable
data class UtilsAboutUpdatePayload(
    val instanceName: String,
    val instanceDescription: String,
)

fun ResultRow.toUtils() = Utils(
    instanceId = this[UtilsModel.instanceId],
    public = this[UtilsModel.public],
    private = this[UtilsModel.private],
    creationDate = this[UtilsModel.creationDate]
)

fun ResultRow.toAbout() = UtilsAbout(
    instanceName = this[UtilsModel.instanceName],
    instanceDescription = this[UtilsModel.instanceDescription],
    creationDate = this[UtilsModel.creationDate]
)