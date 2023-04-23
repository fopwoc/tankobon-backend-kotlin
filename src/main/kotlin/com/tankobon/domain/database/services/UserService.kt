package com.tankobon.domain.database.services

import com.tankobon.api.CredentialsException
import com.tankobon.api.InternalServerError
import com.tankobon.api.UserExistException
import com.tankobon.api.models.UserModel
import com.tankobon.domain.database.models.UserTable
import com.tankobon.domain.database.models.toUser
import com.tankobon.domain.database.models.toUserCredentials
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.callToUserId
import com.tankobon.utils.injectLogger
import io.ktor.server.application.ApplicationCall
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

    private suspend fun getUser(userId: UUID): UserModel = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction UserTable
            .select { UserTable.id eq userId }
            .mapNotNull { it.toUser() }.singleOrNull() ?: throw InternalServerError()
    }

    suspend fun getAllUsers(): List<UserModel> = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction UserTable.selectAll().map { it.toUser() }
    }

    suspend fun callToUser(call: ApplicationCall): UserModel {
        return getUser(callToUserId(call))
    }

    fun addUser(
        username: String,
        password: String,
        isActive: Boolean = true,
        isAdmin: Boolean = false,
    ) = transaction(db = database) {
        if (UserTable.selectAll().andWhere { UserTable.username eq username }.toList().isEmpty()) {
            UserTable.insert {
                it[this.id] = UUID.randomUUID()
                it[this.username] = username
                it[this.password] = BCrypt.hashpw(
                    password,
                    BCrypt.gensalt(ConfigProvider.get().database.bcryptRounds)
                )
                it[this.creation] = System.currentTimeMillis()
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
            val user = UserTable.select { UserTable.username eq username }
                .map { it.toUserCredentials() }.firstOrNull() ?: throw CredentialsException()

            val passwordCheck = BCrypt.checkpw(password, user.password)

            log.debug("password hash for ${user.id} $username check is $passwordCheck")

            if (passwordCheck) {
                return@newSuspendedTransaction user.id
            }
            throw CredentialsException()
        }
    }
}
