package com.tankobon.database

import com.tankobon.database.model.MangaModel
import com.tankobon.database.model.RefreshTokenModel
import com.tankobon.database.model.UserModel
import com.tankobon.database.model.UtilsModel
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
import java.util.*

object DatabaseFactory {

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
                        BCrypt.gensalt(12),
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
                    it[public] = encoder.encodeToString(kp.public.encoded)
                    it[private] = encoder.encodeToString(kp.private.encoded)
                    it[creationDate] = System.currentTimeMillis()
                }
            }

            if (!MangaModel.exists()) {
                create(MangaModel)
            }

            if (!RefreshTokenModel.exists()) {
                create(RefreshTokenModel)
            }
        }
        return serviceDB
    }

    private fun hikariPersist(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:file:./data/serviceDB"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}
