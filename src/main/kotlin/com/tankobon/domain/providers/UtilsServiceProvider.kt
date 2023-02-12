package com.tankobon.domain.providers

import com.tankobon.domain.database.services.UtilsService

class UtilsServiceProvider private constructor() {
    companion object {
        private val instance: UtilsService by lazy {
            UtilsService()
        }

        fun get(): UtilsService {
            return instance
        }
    }
}
