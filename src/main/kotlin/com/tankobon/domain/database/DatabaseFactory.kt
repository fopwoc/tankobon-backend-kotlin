package com.tankobon.domain.database

import com.tankobon.domain.database.models.MangaLibraryModel
import com.tankobon.domain.database.models.RefreshTokenModel
import com.tankobon.domain.database.models.UserModel
import com.tankobon.domain.database.models.UtilsModel
import com.tankobon.domain.providers.ConfigProvider
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64
import java.util.UUID

private const val HIKARI_MAXIMUM_POOL_SIZE = 3
private const val BCRYPT_ROUNDS = 12

class DatabaseFactory {
    fun init(): Database {
        val serviceDB = Database.connect(hikariPersist())
        transaction(serviceDB) {
            // TODO refactor to use UserService.addUser
            if (!UserModel.exists()) {
                create(UserModel)
                UserModel.insert {
                    it[username] = System.getenv("tkbn_username") ?: "user"
                    it[password] = BCrypt.hashpw(
                        System.getenv("tkbn_password") ?: "password",
                        BCrypt.gensalt(BCRYPT_ROUNDS)
                    )
                    it[registerDate] = System.currentTimeMillis()
                    it[active] = true
                    it[admin] = true
                }
            }

            if (!UtilsModel.exists()) {
                create(UtilsModel)
                val encoder: Base64.Encoder = Base64.getEncoder()
                val kp: KeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()

                UtilsModel.insert {
                    it[instanceUuid] = UUID.randomUUID().toString()
                    it[publicKey] = encoder.encodeToString(kp.public.encoded)
                    it[privateKey] = encoder.encodeToString(kp.private.encoded)
                    it[creationDate] = System.currentTimeMillis()
                    it[instanceTitle] = ConfigProvider.get().server.title
                    it[instanceDescription] = ConfigProvider.get().server.description
                }
            }

            if (!MangaLibraryModel.exists()) {
                create(MangaLibraryModel)
            }

            if (!RefreshTokenModel.exists()) {
                create(RefreshTokenModel)
            }
        }
        return serviceDB
    }

    // TODO postgres
    private fun hikariPersist(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:file:./data/serviceDB"
        config.maximumPoolSize = HIKARI_MAXIMUM_POOL_SIZE
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}
