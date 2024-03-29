package com.tankobon.domain.database.services

import com.tankobon.api.CredentialsException
import com.tankobon.api.UserDisabledException
import com.tankobon.api.UserExistException
import com.tankobon.api.models.UserModel
import com.tankobon.api.models.UserUpdatePayloadModel
import com.tankobon.domain.database.models.UserTable
import com.tankobon.domain.database.models.toUser
import com.tankobon.domain.database.models.toUserCredentials
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.utils.dbQuery
import com.tankobon.utils.injectLogger
import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class UserService {

    companion object {
        val log by injectLogger()
    }

    internal suspend fun getUser(userId: UUID): UserModel = dbQuery {
        return@dbQuery UserTable.select { UserTable.id eq userId }.singleOrNull()?.toUser() ?: throw NotFoundException()
    }

    suspend fun getAllUsers(): List<UserModel> = dbQuery {
        return@dbQuery UserTable.selectAll().map { it.toUser() }
    }

    suspend fun addUser(
        username: String,
        password: String,
        isActive: Boolean = true,
        isAdmin: Boolean = false,
    ) = dbQuery {
        if (UserTable.selectAll().andWhere { UserTable.username eq username }.toList().isEmpty()) {
            val time = Clock.System.now()

            UserTable.insert {
                it[this.id] = UUID.randomUUID()
                it[this.username] = username
                it[this.password] = BCrypt.hashpw(
                    password,
                    BCrypt.gensalt(ConfigProvider.get().database.bcryptRounds)
                )
                it[this.creation] = time
                it[this.modified] = time
                it[this.active] = isActive
                it[this.admin] = isAdmin
            }
        } else {
            throw UserExistException()
        }
    }

    suspend fun authUser(username: String, password: String): UUID = dbQuery {
        val user = UserTable.select { UserTable.username eq username }
            .map { it.toUserCredentials() }.firstOrNull() ?: throw CredentialsException()

        val passwordCheck = BCrypt.checkpw(password, user.password)

        if (passwordCheck) {
            if (!user.active) throw UserDisabledException()
            return@dbQuery user.id
        }
        throw CredentialsException()
    }

    suspend fun isUserActive(userId: UUID): Boolean = dbQuery {
        UserTable.select { UserTable.id eq userId }.singleOrNull()?.toUser()?.active ?: throw CredentialsException()
    }

    suspend fun editUser(userUpdate: UserUpdatePayloadModel, isAdmin: Boolean) = dbQuery {
        val user = getUser(userUpdate.id)

        UserTable.update({ UserTable.id eq user.id }) {
            it[this.username] = userUpdate.username
            it[this.password] = BCrypt.hashpw(
                userUpdate.password,
                BCrypt.gensalt(ConfigProvider.get().database.bcryptRounds)
            )
            it[this.admin] = userUpdate.admin
            it[this.active] = if (isAdmin) userUpdate.active else false
            it[this.modified] = Clock.System.now()
        }
    }

    suspend fun toggleUser(userId: UUID) = dbQuery {
        val user = getUser(userId)

        UserTable.update({ UserTable.id eq userId }) {
            it[this.active] = !user.active
            it[this.modified] = Clock.System.now()
        }
    }

    suspend fun deleteUser(userId: UUID) = dbQuery {
        val user = getUser(userId)

        UserTable.deleteWhere { this.id eq user.id }
    }
}
