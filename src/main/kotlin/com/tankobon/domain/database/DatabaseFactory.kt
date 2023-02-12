package com.tankobon.domain.database

import com.tankobon.domain.database.models.MangaLibraryModel
import com.tankobon.domain.database.models.RefreshTokenModel
import com.tankobon.domain.database.models.UserModel
import com.tankobon.domain.database.models.UtilsModel
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.domain.providers.UserServiceProvider
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64
import java.util.UUID

private const val BCRYPT_ROUNDS = 12
class DatabaseFactory {
    fun init() {
        transaction(DatabaseProvider.get()) {
            // TODO refactor to use UserService.addUser
            if (!UserModel.exists()) {
                create(UserModel)
                UserServiceProvider.get().addUser(
                    username = ConfigProvider.get().server.user,
                    password = BCrypt.hashpw(
                        ConfigProvider.get().server.password,
                        BCrypt.gensalt(BCRYPT_ROUNDS)
                    ),
                    isActive = true,
                    isAdmin = true,

                )
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
    }
}
