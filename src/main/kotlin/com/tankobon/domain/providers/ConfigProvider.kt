package com.tankobon.domain.providers

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceOrFileSource
import com.tankobon.domain.models.Config
import java.io.File

class ConfigProvider private constructor() {
    companion object {
        private val instance: Config by lazy {
            ConfigLoaderBuilder
                .default()
                .addResourceOrFileSource(
                    File(
                        System.getenv(
                            "tkbn_config_path"
                        ) ?: "tankobon-config.yml",
                    ).canonicalFile.absolutePath,
                ).build()
                .loadConfigOrThrow()
        }

        fun get(): Config {
            return instance
        }
    }
}
