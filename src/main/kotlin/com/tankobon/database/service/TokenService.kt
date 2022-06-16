package com.tankobon.database.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.database.model.*
import com.tankobon.globalIssuer
import com.tankobon.utils.msOffsetDays
import com.tankobon.utils.sha256
import com.tankobon.utils.toHex
import com.tankobon.webserver.AuthenticationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.interfaces.RSAPrivateKey
import java.util.*

class TokenService(val database: Database) {

    fun getTokenPair(id: String, privateKey: RSAPrivateKey, oldToken: String? = null): TokenPair {
        return runBlocking {
            val currentTime = System.currentTimeMillis()
            val access = createAccessToken(id, privateKey)
            val refresh = sha256(UUID.randomUUID().toString()).toHex()

            withContext(Dispatchers.Default) {
                if (oldToken?.isEmpty() != false) {
                    newSuspendedTransaction(db = database) {
                        RefreshTokenModel.insert {
                            it[uuid] = id
                            it[refreshToken] = refresh
                            it[expires] = currentTime + msOffsetDays(14)
                        }
                    }
                } else {
                    newSuspendedTransaction(db = database) {
                        RefreshTokenModel.update({ RefreshTokenModel.refreshToken eq oldToken }) {
                            it[refreshToken] = refresh
                            it[expires] = currentTime + msOffsetDays(14)
                        }
                    }
                }
            }
            return@runBlocking TokenPair(access, refresh)
        }
    }

    fun getRefreshData(refreshToken: String): RefreshTokenData {
        return transaction(db = database) {
            val newRefreshToken = (
                RefreshTokenModel
                    .select { RefreshTokenModel.refreshToken eq refreshToken }
                    .mapNotNull { toRefreshToken(it) }.singleOrNull()
                )

            return@transaction newRefreshToken ?: throw AuthenticationException()
        }
    }

    private fun toRefreshToken(row: ResultRow): RefreshTokenData {
        return RefreshTokenData(
            uuid = row[RefreshTokenModel.uuid],
            refreshToken = row[RefreshTokenModel.refreshToken],
            expires = row[RefreshTokenModel.expires],
        )
    }

    private fun createAccessToken(uuid: String, privateKey: RSAPrivateKey): String {
        val currentTime = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(globalIssuer)
            .withClaim("uuid", uuid)
            .withExpiresAt(Date(currentTime + msOffsetDays(2)))
            .sign(Algorithm.RSA256(null, privateKey))
    }
}
