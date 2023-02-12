package com.tankobon.domain.providers

import com.tankobon.domain.database.services.UserService

class UserServiceProvider private constructor() {
    companion object {
        private val instance: UserService by lazy {
            UserService()
        }

        fun get(): UserService {
            return instance
        }
    }
}
