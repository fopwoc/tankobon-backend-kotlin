package com.tankobon.domain.database.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.api.CredentialsException
import com.tankobon.api.models.TokenPairModel
import com.tankobon.domain.database.models.RefreshTokenTable
import com.tankobon.domain.database.models.toRefreshTokenData
import com.tankobon.domain.models.TokenData
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.domain.providers.InstanceServiceProvider
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
import java.util.Date
import java.util.UUID

class TokenService {
    val database = DatabaseProvider.get()

    fun getTokenPair(
        userId: UUID,
        userAgent: String? = null,
        userIP: String? = null,
        oldToken: String? = null,
    ): TokenPairModel {
        return runBlocking {
            val currentTime = System.currentTimeMillis()
            val access = createAccessToken(userId)
            val refresh = sha256(UUID.randomUUID().toString()).toHex()

            val expireRefresh = ConfigProvider.get().api.expire.refresh

            withContext(Default) {
                if (oldToken.isNullOrEmpty()) {
                    newSuspendedTransaction(db = database) {
                        RefreshTokenTable.insert {
                            it[this.id] = UUID.randomUUID()
                            it[this.userId] = userId
                            it[this.userAgent] = userAgent ?: "unknown"
                            it[this.userIP] = userIP ?: "unknown"
                            it[this.refreshToken] = refresh
                            it[this.expires] = if (expireRefresh != 0) { currentTime + expireRefresh } else { 0 }
                        }
                    }
                } else {
                    newSuspendedTransaction(db = database) {
                        RefreshTokenTable.update({ RefreshTokenTable.refreshToken eq oldToken }) {
                            it[this.refreshToken] = refresh
                            it[this.expires] = if (expireRefresh != 0) { currentTime + expireRefresh } else { 0 }
                        }
                    }
                }
            }
            return@runBlocking TokenPairModel(InstanceService().getInstanceId(), access, refresh)
        }
    }

    fun getRefreshData(refreshToken: String): TokenData {
        return transaction(db = database) {
            val newRefreshToken = (
                RefreshTokenTable
                    .select { RefreshTokenTable.refreshToken eq refreshToken }
                    .mapNotNull { it.toRefreshTokenData() }.singleOrNull()
                )

            return@transaction newRefreshToken ?: throw CredentialsException()
        }
    }

    suspend fun deleteRefreshData(refreshToken: String) {
        return newSuspendedTransaction(db = database) {
            RefreshTokenTable
                .deleteWhere { RefreshTokenTable.refreshToken eq refreshToken }
        }
    }

    private fun createAccessToken(userId: UUID): String {
        val currentTime = System.currentTimeMillis()
        val expireAccess = ConfigProvider.get().api.expire.access
        return JWT.create()
            .withIssuer(ConfigProvider.get().api.issuer)
            .withClaim("userId", userId.toString())
            .withExpiresAt(Date(if (expireAccess != 0) { currentTime + expireAccess } else { 0 }))
            .sign(Algorithm.RSA256(null, InstanceServiceProvider.get().getPrivateKey()))
    }
}
