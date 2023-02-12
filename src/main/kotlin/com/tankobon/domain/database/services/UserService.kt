package com.tankobon.domain.database.services

import com.tankobon.api.CredentialsException
import com.tankobon.api.InternalServerError
import com.tankobon.api.UserExistException
import com.tankobon.api.models.User
import com.tankobon.domain.database.models.UserModel
import com.tankobon.domain.database.models.toUser
import com.tankobon.domain.database.models.toUserHash
import com.tankobon.domain.models.UserHash
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.uuidFromString
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserService {
    val database = DatabaseProvider.get()

    suspend fun getUser(uuid: String): User = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction UserModel
            .select { UserModel.id eq uuidFromString(uuid.replace("\"", "")) }
            .mapNotNull { it.toUser() }.singleOrNull() ?: throw InternalServerError()
    }

    fun addUser(username: String, password: String, isActive: Boolean? = null, isAdmin: Boolean? = null) = transaction(db = database) {
        if (UserModel.selectAll().andWhere { UserModel.username eq username }.toList().isEmpty()) {
            UserModel.insert {
                it[this.username] = username
                it[this.password] = BCrypt.hashpw(password, BCrypt.gensalt())
                it[registerDate] = System.currentTimeMillis()
                it[active] = isActive ?: true
                it[admin] = isAdmin ?: false
            }
        } else {
            throw UserExistException()
        }
    }

    suspend fun authUser(username: String, password: String): String {
        return newSuspendedTransaction(db = database) {
            val userHash: UserHash = UserModel.select { UserModel.username eq username }
                .map { it.toUserHash() }.firstOrNull() ?: throw CredentialsException()

            if (BCrypt.checkpw(password, userHash.password)) {
                return@newSuspendedTransaction userHash.id
            }
            throw CredentialsException()
        }
    }
}
