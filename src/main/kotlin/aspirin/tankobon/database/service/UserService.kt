package aspirin.tankobon.database.service

import aspirin.tankobon.database.model.User
import aspirin.tankobon.database.model.UserHash
import aspirin.tankobon.database.model.UserModel
import aspirin.tankobon.webserver.AuthenticationException
import aspirin.tankobon.webserver.UserExistException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class UserService(val database: Database) {

    suspend fun getUser(uuid: String): User = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction toUser(
            UserModel.select { UserModel.id eq UUID.fromString(uuid.replace("\"", "")) }.first()
        )
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
            val userHash: UserHash = UserModel.select { UserModel.username eq username }.map { toHash(it) }.first()
            if (BCrypt.checkpw(password, userHash.password)) {
                return@newSuspendedTransaction userHash.id
            }
            throw AuthenticationException()
        }
    }

    private fun toUser(row: ResultRow): User {
        return User(
            id = row[UserModel.id].toString(),
            username = row[UserModel.username],
            registerDate = row[UserModel.registerDate],
            active = row[UserModel.active],
            admin = row[UserModel.admin],
        )
    }

    private fun toHash(row: ResultRow): UserHash {
        return UserHash(
            id = row[UserModel.id].toString(),
            password = row[UserModel.password]
        )
    }
}