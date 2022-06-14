package com.tankobon.database.service

import com.tankobon.database.model.Utils
import com.tankobon.database.model.UtilsModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

class UtilService(val database: Database) {
    private val decoder: Base64.Decoder = Base64.getDecoder()
    private val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
    fun getPublicKey(): RSAPublicKey = transaction(db = database) {
        keyFactory.generatePublic(
            X509EncodedKeySpec(
                decoder.decode(
                    UtilsModel.selectAll().map { toUtils(it) }.first().public
                )
            )
        ) as RSAPublicKey
    }

    fun getPrivateKey(): RSAPrivateKey = transaction(db = database) {
        keyFactory.generatePrivate(
            PKCS8EncodedKeySpec(
                decoder.decode(
                    UtilsModel.selectAll().map { toUtils(it) }.first().private
                )
            )
        ) as RSAPrivateKey
    }

    private fun toUtils(row: ResultRow): Utils {
        return Utils(
            public = row[UtilsModel.public],
            private = row[UtilsModel.private],
            creationDate = row[UtilsModel.creationDate]
        )
    }
}
