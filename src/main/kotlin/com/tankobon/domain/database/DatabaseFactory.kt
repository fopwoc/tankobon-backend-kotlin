package com.tankobon.domain.database

import com.tankobon.domain.database.models.MangaPageModel
import com.tankobon.domain.database.models.MangaTitleModel
import com.tankobon.domain.database.models.MangaVolumeModel
import com.tankobon.domain.database.models.RefreshTokenModel
import com.tankobon.domain.database.models.UserModel
import com.tankobon.domain.database.models.UtilsModel
import com.tankobon.domain.providers.ConfigProvider
import com.tankobon.domain.providers.DatabaseProvider
import com.tankobon.domain.providers.UserServiceProvider
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.createSchema
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64

class DatabaseFactory {

    fun init() {
        transaction(DatabaseProvider.get()) {
            val schema = Schema(ConfigProvider.get().database.schema)
            if (!schema.exists()) {
                createSchema(schema)
            }

            if (!UserModel.exists()) {
                create(UserModel)
                UserServiceProvider.get().addUser(
                    username = ConfigProvider.get().server.user,
                    password = ConfigProvider.get().server.password,
                    isActive = true,
                    isAdmin = true,
                )
            }

            if (!UtilsModel.exists()) {
                create(UtilsModel)
                val encoder: Base64.Encoder = Base64.getEncoder()
                val kp: KeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()

                UtilsModel.insert {
                    it[this.publicKey] = encoder.encodeToString(kp.public.encoded)
                    it[this.privateKey] = encoder.encodeToString(kp.private.encoded)
                    it[this.creationDate] = System.currentTimeMillis()
                    it[this.instanceTitle] = ConfigProvider.get().server.title
                    it[this.instanceDescription] = ConfigProvider.get().server.description
                }
            }

            if (!MangaTitleModel.exists()) {
                create(MangaTitleModel)
            }

            if (!MangaVolumeModel.exists()) {
                create(MangaVolumeModel)
            }

            if (!MangaPageModel.exists()) {
                create(MangaPageModel)
            }

            if (!RefreshTokenModel.exists()) {
                create(RefreshTokenModel)
            }
        }
    }
}
