package com.tankobon.domain.providers

import com.tankobon.domain.database.services.InstanceService

class InstanceServiceProvider private constructor() {
    companion object {
        private val instance: InstanceService by lazy {
            InstanceService()
        }

        fun get(): InstanceService {
            return instance
        }
    }
}
