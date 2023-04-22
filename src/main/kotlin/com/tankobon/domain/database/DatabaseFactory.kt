package com.tankobon.domain.database

import com.tankobon.domain.database.models.InstanceTable
import com.tankobon.domain.database.models.MangaPageTable
import com.tankobon.domain.database.models.MangaTitleTable
import com.tankobon.domain.database.models.MangaVolumeTable
import com.tankobon.domain.database.models.RefreshTokenTable
import com.tankobon.domain.database.models.UserTable
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
import java.util.UUID

class DatabaseFactory {

    fun init() {
        transaction(DatabaseProvider.get()) {
            val schema = Schema(ConfigProvider.get().database.schema)
            if (!schema.exists()) {
                createSchema(schema)
            }

            if (!UserTable.exists()) {
                create(UserTable)
                UserServiceProvider.get().addUser(
                    username = ConfigProvider.get().server.user,
                    password = ConfigProvider.get().server.password,
                    isActive = true,
                    isAdmin = true,
                )
            }

            if (!InstanceTable.exists()) {
                create(InstanceTable)
                val encoder: Base64.Encoder = Base64.getEncoder()
                val kp: KeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()

                InstanceTable.insert {
                    it[this.id] = UUID.randomUUID()
                    it[this.publicKey] = encoder.encodeToString(kp.public.encoded)
                    it[this.privateKey] = encoder.encodeToString(kp.private.encoded)
                    it[this.title] = ConfigProvider.get().server.title
                    it[this.description] = ConfigProvider.get().server.description
                    it[this.creation] = System.currentTimeMillis()
                    it[this.modified] = System.currentTimeMillis()
                }
            }

            if (!MangaTitleTable.exists()) {
                create(MangaTitleTable)
            }

            if (!MangaVolumeTable.exists()) {
                create(MangaVolumeTable)
            }

            if (!MangaPageTable.exists()) {
                create(MangaPageTable)
            }

            if (!RefreshTokenTable.exists()) {
                create(RefreshTokenTable)
            }
        }
    }
}
