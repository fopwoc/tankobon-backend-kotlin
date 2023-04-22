package com.tankobon.domain.database.models

import com.tankobon.api.models.InstanceAboutModel
import com.tankobon.domain.models.Instance
import com.tankobon.domain.providers.ConfigProvider
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

private const val UTILS_MODEL_INSTANCE_TITLE = 64

object InstanceTable : IdTable<UUID>(
    name = "${ConfigProvider.get().database.schema}.instance"
) {
    override val id = uuid("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val publicKey = text("public_key")
    val privateKey = text("private_key")
    val title = varchar("instance_title", UTILS_MODEL_INSTANCE_TITLE)
    val description = text("instance_description")
    val creation = long("creation_date")
    val modified = long("modified_date")
}

fun ResultRow.toInstance() = Instance(
    id = this[InstanceTable.id].value,
    publicKey = this[InstanceTable.publicKey],
    privateKey = this[InstanceTable.privateKey],
    title = this[InstanceTable.title],
    description = this[InstanceTable.description],
    creation = this[InstanceTable.creation],
    modified = this[InstanceTable.modified],
)

fun ResultRow.toInstanceAbout() = InstanceAboutModel(
    id = this[InstanceTable.id].value,
    title = this[InstanceTable.title],
    description = this[InstanceTable.description],
)
