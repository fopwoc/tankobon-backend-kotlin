package com.tankobon.domain.database.services

import com.tankobon.api.models.InstanceAboutModel
import com.tankobon.api.models.InstanceAboutUpdatePayloadModel
import com.tankobon.domain.database.models.InstanceTable
import com.tankobon.domain.database.models.toInstance
import com.tankobon.domain.database.models.toInstanceAbout
import com.tankobon.utils.dbQuery
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.UUID

class InstanceService {
    private val decoder: Base64.Decoder = Base64.getDecoder()
    private val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")

    suspend fun getPublicKey(): RSAPublicKey = dbQuery {
        keyFactory.generatePublic(
            X509EncodedKeySpec(
                decoder.decode(
                    InstanceTable.selectAll().map { it.toInstance() }.first().publicKey
                )
            )
        ) as RSAPublicKey
    }

    suspend fun getPrivateKey(): RSAPrivateKey = dbQuery {
        keyFactory.generatePrivate(
            PKCS8EncodedKeySpec(
                decoder.decode(
                    InstanceTable.selectAll().map { it.toInstance() }.first().privateKey
                )
            )
        ) as RSAPrivateKey
    }

    suspend fun getInstanceId(): UUID = dbQuery {
        InstanceTable.selectAll().map { it.toInstance() }.first().id
    }

    suspend fun getAbout(): InstanceAboutModel = dbQuery {
        InstanceTable.selectAll().map { it.toInstanceAbout() }.first()
    }

    suspend fun setAbout(
        payload: InstanceAboutUpdatePayloadModel,
    ) = dbQuery {
        val instanceId = getInstanceId()
        InstanceTable.update({ InstanceTable.id eq instanceId }) {
            it[this.title] = payload.title
            it[this.description] = payload.description
            it[this.modified] = Clock.System.now()
        }
    }

    suspend fun instanceModifiedUpdate() = dbQuery {
        val instanceId = getInstanceId()
        InstanceTable.update({ InstanceTable.id eq instanceId }) {
            it[this.modified] = Clock.System.now()
        }
    }
}
