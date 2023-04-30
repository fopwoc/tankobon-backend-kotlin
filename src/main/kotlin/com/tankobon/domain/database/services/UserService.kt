package com.tankobon.domain.database.services

import com.tankobon.api.CredentialsException
import com.tankobon.api.UserDisabledException
import com.tankobon.api.UserExistException
import com.tankobon.api.models.UserModel
import com.tankobon.domain.database.models.UserTable
import com.tankobon.domain.database.models.toUser
import com.tankobon.domain.database.models.toUserCredentials
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.utils.callToUserId
import com.tankobon.utils.dbQuery
import com.tankobon.utils.injectLogger
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class UserService {

    companion object {
        val log by injectLogger()
    }

    val database = DatabaseProvider.get()

    private suspend fun getUser(userId: UUID): UserModel = dbQuery {
        return@dbQuery UserTable.select { UserTable.id eq userId }.singleOrNull()?.toUser() ?: throw NotFoundException()
    }

    suspend fun getAllUsers(): List<UserModel> = dbQuery {
        return@dbQuery UserTable.selectAll().map { it.toUser() }
    }

    suspend fun callToUser(call: ApplicationCall): UserModel {
        return getUser(callToUserId(call))
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
}
