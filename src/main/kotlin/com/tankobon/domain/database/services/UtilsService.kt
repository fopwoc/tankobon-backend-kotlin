package com.tankobon.domain.database.services

import com.tankobon.api.models.UtilsAbout
import com.tankobon.api.models.UtilsAboutUpdatePayload
import com.tankobon.domain.database.models.UtilsModel
import com.tankobon.domain.database.models.toAbout
import com.tankobon.domain.database.models.toUtils
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
import java.util.*

class UtilsService {
    val database = DatabaseProvider.get()

    private val decoder: Base64.Decoder = Base64.getDecoder()
    private val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")

    fun getPublicKey(): RSAPublicKey = transaction(db = database) {
        keyFactory.generatePublic(
            X509EncodedKeySpec(
                decoder.decode(
                    UtilsModel.selectAll().map { it.toUtils() }.first().public
                )
            )
        ) as RSAPublicKey
    }

    fun getPrivateKey(): RSAPrivateKey = transaction(db = database) {
        keyFactory.generatePrivate(
            PKCS8EncodedKeySpec(
                decoder.decode(
                    UtilsModel.selectAll().map { it.toUtils() }.first().private
                )
            )
        ) as RSAPrivateKey
    }

    fun getInstanceId(): String = transaction(db = database) {
        UtilsModel.selectAll().map { it.toUtils() }.first().instanceUuid
    }

    suspend fun getAbout(): UtilsAbout = newSuspendedTransaction(db = database) {
        UtilsModel.selectAll().map { it.toAbout() }.first()
    }

    suspend fun setAbout(payload: UtilsAboutUpdatePayload) = newSuspendedTransaction(db = database) {
        UtilsModel.update({ UtilsModel.instanceUuid.isNotNull() }) {
            it[instanceTitle] = payload.instanceName
            it[instanceDescription] = payload.instanceDescription
        }
    }
}
