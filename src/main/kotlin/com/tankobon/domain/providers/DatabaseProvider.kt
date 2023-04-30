package com.tankobon.domain.providers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

class DatabaseProvider private constructor() {

    companion object {
        private val instance: Database by lazy {
            Database.connect(
                HikariDataSource(
                    HikariConfig().apply {
                        driverClassName = "org.postgresql.Driver"
                        jdbcUrl = ConfigProvider.get().database.url
                        username = ConfigProvider.get().database.user
                        password = ConfigProvider.get().database.password
                        maximumPoolSize = 3
                        validate()
                    }
                )
            )
        }

        fun get(): Database {
            return instance
        }
    }
}
