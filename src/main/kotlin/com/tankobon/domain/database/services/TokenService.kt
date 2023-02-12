package com.tankobon.domain.database.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.api.CredentialsException
import com.tankobon.api.models.RefreshTokenData
import com.tankobon.api.models.TokenPair
import com.tankobon.domain.database.models.RefreshTokenModel
import com.tankobon.domain.database.models.toRefreshTokenData
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.daysToMs
import com.tankobon.utils.sha256
import com.tankobon.utils.toHex
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

private const val REFRESH_TOKEN_EXPIRE_DAYS = 14
private const val ACCESS_TOKEN_EXPIRE_DAYS = 1

class TokenService {
    val database = DatabaseProvider.get()

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
                            it[expires] = currentTime + daysToMs(REFRESH_TOKEN_EXPIRE_DAYS)
                        }
                    }
                } else {
                    newSuspendedTransaction(db = database) {
                        RefreshTokenModel.update({ RefreshTokenModel.refreshToken eq oldToken }) {
                            it[refreshToken] = refresh
                            it[expires] = currentTime + daysToMs(REFRESH_TOKEN_EXPIRE_DAYS)
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
            .withIssuer(ConfigProvider.get().api.issuer)
            .withClaim("uuid", uuid)
            .withExpiresAt(Date(currentTime + daysToMs(ACCESS_TOKEN_EXPIRE_DAYS)))
            .sign(Algorithm.RSA256(null, privateKey))
    }
}
