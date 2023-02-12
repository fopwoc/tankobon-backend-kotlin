package com.tankobon.domain.providers

import com.tankobon.domain.database.services.TokenService

class TokenServiceProvider private constructor() {
    companion object {
        private val instance: TokenService by lazy {
            TokenService()
        }

        fun get(): TokenService {
            return instance
        }
    }
}
