package com.tankobon.database.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.database.DatabaseInstance
import com.tankobon.database.model.RefreshTokenData
import com.tankobon.database.model.RefreshTokenModel
import com.tankobon.database.model.TokenPair
import com.tankobon.database.model.toRefreshTokenData
import com.tankobon.globalIssuer
import com.tankobon.utils.msOffsetDays
import com.tankobon.utils.sha256
import com.tankobon.utils.toHex
import com.tankobon.webserver.CredentialsException
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.security.interfaces.RSAPrivateKey
import java.util.*

class TokenService {
    val database = DatabaseInstance.instance
    fun getTokenPair(id: String, privateKey: RSAPrivateKey, oldToken: String? = null): TokenPair {
        return runBlocking {
            val currentTime = System.currentTimeMillis()
            val access = createAccessToken(id, privateKey)
            val refresh = sha256(UUID.randomUUID().toString()).toHex()

            withContext(Default) {
                if (oldToken.isNullOrEmpty()) {
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
            return@runBlocking TokenPair(UtilsService().getInstanceId(), access, refresh)
        }
    }

    fun getRefreshData(refreshToken: String): RefreshTokenData {
        return transaction(db = database) {
            val newRefreshToken = (
                RefreshTokenModel
                    .select { RefreshTokenModel.refreshToken eq refreshToken }
                    .mapNotNull { it.toRefreshTokenData() }.singleOrNull()
                )

            return@transaction newRefreshToken ?: throw CredentialsException()
        }
    }

    suspend fun deleteRefreshData(refreshToken: String) {
        return newSuspendedTransaction(db = database) {
            RefreshTokenModel
                .deleteWhere { RefreshTokenModel.refreshToken eq refreshToken }
        }
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
