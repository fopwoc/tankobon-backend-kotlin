package com.tankobon.domain.database.services

import com.tankobon.api.CredentialsException
import com.tankobon.api.InternalServerError
import com.tankobon.api.UserExistException
import com.tankobon.api.models.User
import com.tankobon.domain.database.models.UserModel
import com.tankobon.domain.database.models.toUser
import com.tankobon.domain.database.models.toUserHash
import com.tankobon.domain.models.UserHash
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.injectLogger
import com.tankobon.utils.uuidFromString
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class UserService {

    companion object {
        val log by injectLogger()
    }

    val database = DatabaseProvider.get()

    suspend fun getUser(userId: String): User = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction UserModel
            .select { UserModel.id eq uuidFromString(userId.replace("\"", "")) }
            .mapNotNull { it.toUser() }.singleOrNull() ?: throw InternalServerError()
    }

    suspend fun getUserCall(call: ApplicationCall): User = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction getUser(
            call.principal<JWTPrincipal>()?.payload?.getClaim("userId").toString()
        )
    }

    fun addUser(
        username: String,
        password: String,
        isActive: Boolean = true,
        isAdmin: Boolean = false,
    ) = transaction(db = database) {
        if (UserModel.selectAll().andWhere { UserModel.username eq username }.toList().isEmpty()) {
            UserModel.insert {
                it[this.username] = username
                it[this.password] = BCrypt.hashpw(
                    password,
                    BCrypt.gensalt(ConfigProvider.get().database.bcryptRounds)
                )
                it[this.created] = System.currentTimeMillis()
                it[this.modified] = System.currentTimeMillis()
                it[this.active] = isActive
                it[this.admin] = isAdmin
            }
        } else {
            throw UserExistException()
        }
    }

    suspend fun authUser(username: String, password: String): UUID {
        return newSuspendedTransaction(db = database) {
            val userHash: UserHash = UserModel.select { UserModel.username eq username }
                .map { it.toUserHash() }.firstOrNull() ?: throw CredentialsException()

            val passwordCheck = BCrypt.checkpw(password, userHash.password)

            log.debug("password hash for ${userHash.id} $username check is $passwordCheck")

            if (passwordCheck) {
                return@newSuspendedTransaction userHash.id
            }
            throw CredentialsException()
        }
    }
}
