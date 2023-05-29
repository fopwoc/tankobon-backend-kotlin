package com.tankobon.domain.providers

import com.tankobon.domain.database.services.MangaService

class MangaServiceProvider private constructor() {
    companion object {
        private val instance: MangaService by lazy {
            MangaService()
        }

        fun get(): MangaService {
            return instance
        }
    }
}

