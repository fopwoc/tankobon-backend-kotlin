package aspirin.tankobon.database.service

import aspirin.tankobon.database.model.User
import aspirin.tankobon.database.model.UserModel
import aspirin.tankobon.webserver.AuthenticationException
import aspirin.tankobon.webserver.UserExistException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
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
                it[password] = newPassword
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
            val userQuery: Query = UserModel.select(UserModel.username.eq(username) and UserModel.password.eq(password))
            if (userQuery.empty()) {
                throw AuthenticationException()
            }
            return@newSuspendedTransaction userQuery.first()[UserModel.id].toString()
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
}