package com.tankobon.domain.database.services

import com.tankobon.api.models.InstanceAboutModel
import com.tankobon.api.models.InstanceAboutUpdatePayloadModel
import com.tankobon.domain.database.models.InstanceTable
import com.tankobon.domain.database.models.toInstance
import com.tankobon.domain.database.models.toInstanceAbout
import com.tankobon.domain.providers.DatabaseProvider
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.UUID

class InstanceService {
    val database = DatabaseProvider.get()

    private val decoder: Base64.Decoder = Base64.getDecoder()
    private val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")

    fun getPublicKey(): RSAPublicKey = transaction(db = database) {
        keyFactory.generatePublic(
            X509EncodedKeySpec(
                decoder.decode(
                    InstanceTable.selectAll().map { it.toInstance() }.first().publicKey
                )
            )
        ) as RSAPublicKey
    }

    fun getPrivateKey(): RSAPrivateKey = transaction(db = database) {
        keyFactory.generatePrivate(
            PKCS8EncodedKeySpec(
                decoder.decode(
                    InstanceTable.selectAll().map { it.toInstance() }.first().privateKey
                )
            )
        ) as RSAPrivateKey
    }

    fun getInstanceId(): UUID = transaction(db = database) {
        InstanceTable.selectAll().map { it.toInstance() }.first().id
    }

    suspend fun getAbout(): InstanceAboutModel = newSuspendedTransaction(db = database) {
        InstanceTable.selectAll().map { it.toInstanceAbout() }.first()
    }

    suspend fun setAbout(
        payload: InstanceAboutUpdatePayloadModel,
    ) = newSuspendedTransaction(db = database) {
        InstanceTable.update {
            it[this.title] = payload.title
            it[this.description] = payload.description
        }
    }
}
