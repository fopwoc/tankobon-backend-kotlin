package com.tankobon.domain.providers

import com.tankobon.domain.database.services.LastPointService

class LastPointServiceProvider private constructor() {
    companion object {
        private val instance: LastPointService by lazy {
            LastPointService()
        }

        fun get(): LastPointService {
            return instance
        }
    }
}
