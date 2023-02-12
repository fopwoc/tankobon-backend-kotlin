package com.tankobon.domain.providers

import org.jetbrains.exposed.sql.Database

class DatabaseProvider private constructor() {
    companion object {
        private val instance: Database by lazy {
            Database.connect(
                url = ConfigProvider.get().database.url,
                driver = "com.impossibl.postgres.jdbc.PGDriver",
                user = ConfigProvider.get().database.user,
                password = ConfigProvider.get().database.password,
            )
        }

        fun get(): Database {
            return instance
        }
    }
}
