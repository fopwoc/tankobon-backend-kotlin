package com.tankobon.domain.providers

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.parsers.defaultParserRegistry
import com.sksamuel.hoplite.yaml.YamlParser
import com.tankobon.domain.models.Config
import java.io.File

class ConfigProvider private constructor() {
    companion object {
        private val instance: Config by lazy {
            ConfigLoader().loadConfigOrThrow(
                File(
                    System.getenv(
                        "tkbn_config_path"
                    ) ?: "tankobon-config.yml",
                ).canonicalFile.absolutePath
            )
        }

        fun get(): Config {
            return instance
        }
    }
}
