package com.tankobon.database.service

import com.tankobon.database.DatabaseInstance
import com.tankobon.database.model.UtilsAbout
import com.tankobon.database.model.UtilsAboutUpdatePayload
import com.tankobon.database.model.UtilsModel
import com.tankobon.database.model.toAbout
import com.tankobon.database.model.toUtils
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
    val database = DatabaseInstance.instance
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
        UtilsModel.selectAll().map { it.toUtils() }.first().instanceId
    }

    suspend fun getAbout(): UtilsAbout = newSuspendedTransaction(db = database) {
        UtilsModel.selectAll().map { it.toAbout() }.first()
    }

    suspend fun setAbout(payload: UtilsAboutUpdatePayload) = newSuspendedTransaction(db = database) {
        UtilsModel.update({ UtilsModel.instanceId.isNotNull() }) {
            it[instanceName] = payload.instanceName
            it[instanceDescription] = payload.instanceDescription
        }
    }
}
