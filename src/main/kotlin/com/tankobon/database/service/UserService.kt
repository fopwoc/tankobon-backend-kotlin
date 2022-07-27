package com.tankobon.database.service

import com.tankobon.database.DatabaseInstance
import com.tankobon.database.model.User
import com.tankobon.database.model.UserHash
import com.tankobon.database.model.UserModel
import com.tankobon.database.model.toUser
import com.tankobon.database.model.toUserHash
import com.tankobon.webserver.AuthenticationException
import com.tankobon.webserver.InternalServerError
import com.tankobon.webserver.UserExistException
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class UserService() {
    val database = DatabaseInstance.instance;

    suspend fun getUser(uuid: String): User = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction UserModel
            .select { UserModel.id eq UUID.fromString(uuid.replace("\"", "")) }
            .mapNotNull { it.toUser() }.singleOrNull() ?: throw InternalServerError()
    }

    fun addUser(newUsername: String, newPassword: String, newAdmin: Boolean? = null) = transaction(db = database) {
        if (UserModel.selectAll().andWhere { UserModel.username eq newUsername }.toList().isEmpty()) {
            UserModel.insert {
                it[username] = newUsername
                it[password] = BCrypt.hashpw(newPassword, BCrypt.gensalt())
                it[registerDate] = System.currentTimeMillis()
                it[active] = true
                it[admin] = newAdmin ?: false
            }
        } else {
            throw UserExistException()
        }
    }

    suspend fun authUser(username: String, password: String): String {
        return newSuspendedTransaction(db = database) {
            val userHash: UserHash = UserModel.select { UserModel.username eq username }
                .map { it.toUserHash() }.first()

            if (BCrypt.checkpw(password, userHash.password)) {
                return@newSuspendedTransaction userHash.id
            }
            throw AuthenticationException()
        }
    }
}
