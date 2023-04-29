package com.tankobon.domain.database.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.api.CredentialsException
import com.tankobon.api.InternalServerError
import com.tankobon.api.models.TokenInfoModel
import com.tankobon.api.models.TokenPairModel
import com.tankobon.api.models.UserModel
import com.tankobon.domain.database.models.TokenTable
import com.tankobon.domain.database.models.toRefreshTokenData
import com.tankobon.domain.database.models.toTokenInfo
import com.tankobon.domain.models.TokenClaim
import com.tankobon.domain.models.TokenData
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.domain.providers.InstanceServiceProvider
import com.tankobon.utils.dbQuery
import com.tankobon.utils.injectLogger
import com.tankobon.utils.sha256
import com.tankobon.utils.toHex
import io.ktor.server.plugins.NotFoundException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.Date
import java.util.UUID
import kotlinx.datetime.Clock

class TokenService {
    companion object {
        val log by injectLogger()
    }

    suspend fun getTokenPair(
        userId: UUID,
        userAgent: String? = null,
        userIP: String? = null,
        oldToken: String? = null,
    ): TokenPairModel {
        val currentTime = Clock.System.now()
        val refresh = sha256(UUID.randomUUID().toString()).toHex()
        var tokenId: UUID? = null

        if (oldToken.isNullOrEmpty()) {
            tokenId = UUID.randomUUID()
            dbQuery {
                TokenTable.insert {
                    it[this.id] = tokenId ?: throw InternalServerError()
                    it[this.userId] = userId
                    it[this.userAgent] = userAgent ?: "unknown"
                    it[this.userIP] = userIP ?: "unknown"
                    it[this.refreshToken] = refresh
                    it[this.creation] = currentTime
                    it[this.modified] = currentTime
                }
            }
        } else {
            dbQuery {
                tokenId = TokenTable.select { TokenTable.refreshToken eq oldToken }
                    .firstOrNull()?.toRefreshTokenData()?.id ?: throw NotFoundException()

                TokenTable.update({ TokenTable.refreshToken eq oldToken }) {
                    it[this.refreshToken] = refresh
                    it[this.modified] = currentTime
                }
            }
        }

        return TokenPairModel(
            tokenId ?: throw InternalServerError(),
            InstanceService().getInstanceId(),
            createAccessToken(tokenId ?: throw InternalServerError(), userId),
            refresh,
        )
    }

    suspend fun getRefreshData(refreshToken: String): TokenData = dbQuery {
        return@dbQuery (
            TokenTable
                .select { TokenTable.refreshToken eq refreshToken }
                .mapNotNull { it.toRefreshTokenData() }.singleOrNull()
            ) ?: throw CredentialsException()
    }

    suspend fun deleteTokens(tokenId: UUID, userId: UUID) = dbQuery {
        val token = TokenTable.select { TokenTable.id eq tokenId }.singleOrNull()?.toRefreshTokenData()

        if (token?.userId == userId) {
            TokenTable.deleteWhere { this.id eq tokenId }
        } else {
            throw NotFoundException()
        }
    }

    suspend fun cleanupRefreshTokens() = dbQuery {
        val currentTime = System.currentTimeMillis()
        val expireRefresh = ConfigProvider.get().api.expire.refresh
        if (expireRefresh != 0) TokenTable.deleteWhere { this.modified greater (currentTime + expireRefresh) }
    }

    private suspend fun createAccessToken(tokenId: UUID, userId: UUID): String {
        val currentTime = System.currentTimeMillis()
        val expireAccess = ConfigProvider.get().api.expire.access
        return JWT.create()
            .withIssuer(ConfigProvider.get().api.issuer)
            .withClaim(TokenClaim.TOKEN_ID.name, tokenId.toString())
            .withClaim(TokenClaim.USER_ID.name, userId.toString())
            .withExpiresAt(Date(if (expireAccess != 0) { currentTime + expireAccess } else { 0 }))
            .sign(Algorithm.RSA256(null, InstanceServiceProvider.get().getPrivateKey()))
    }

    suspend fun checkCredentials(tokenId: UUID, userId: UUID): Boolean = dbQuery {
        return@dbQuery TokenTable.select {
            TokenTable.id eq tokenId and (TokenTable.userId eq userId)
        }.singleOrNull() != null
    }

    suspend fun getUserTokens(user: UserModel): List<TokenInfoModel> = dbQuery {
        return@dbQuery TokenTable.select { TokenTable.userId eq user.id }
            .sortedByDescending { TokenTable.creation }.map { it.toTokenInfo() }
    }
}
