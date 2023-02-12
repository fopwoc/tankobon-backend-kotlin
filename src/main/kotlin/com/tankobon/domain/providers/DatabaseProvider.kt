package com.tankobon.domain.providers

import com.tankobon.domain.database.DatabaseFactory
import org.jetbrains.exposed.sql.Database

class DatabaseProvider private constructor() {
    companion object {
        private val instance: Database by lazy {
            DatabaseFactory().init()
        }

        fun get(): Database {
            return instance
        }
    }
}
