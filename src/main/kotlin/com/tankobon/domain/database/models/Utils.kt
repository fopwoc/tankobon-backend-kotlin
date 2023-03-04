package com.tankobon.domain.database.models

import com.tankobon.api.models.UtilsAbout
import com.tankobon.domain.models.Utils
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow

private const val UTILS_MODEL_INSTANCE_TITLE = 64

object UtilsModel : UUIDTable(
    name = "${ConfigProvider.get().database.schema}.utils"
) {
    val publicKey = text("public_key")
    val privateKey = text("private_key")
    var creationDate = long("creation_date")
    var instanceTitle = varchar("instance_title", UTILS_MODEL_INSTANCE_TITLE)
    var instanceDescription = text("instance_description")
}

fun ResultRow.toUtils() = Utils(
    instanceId = this[UtilsModel.id].value,
    public = this[UtilsModel.publicKey],
    private = this[UtilsModel.privateKey],
    creationDate = this[UtilsModel.creationDate],
)

fun ResultRow.toAbout() = UtilsAbout(
    instanceName = this[UtilsModel.instanceTitle],
    instanceDescription = this[UtilsModel.instanceDescription],
    creationDate = this[UtilsModel.creationDate],
)
