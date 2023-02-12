package com.tankobon.domain.models

import java.io.File

data class Config(
    val server: ConfigServerInstance,
    val database: ConfigDatabase,
    val api: ConfigApi,
    val library: ConfigLibrary,
)

data class ConfigServerInstance(
    val title: String,
    val description: String,
    val user: String = "user",
    val password: String = "password",
)

data class ConfigDatabase(
    val url: String,
    val user: String,
    val password: String,
)

data class ConfigApi(
    val address: String,
    val port: Int,
    val issuer: String = "http://$address:$port/",
)

data class ConfigLibrary(
    private val manga: String,
    val mangaFile: File = File(manga).canonicalFile,

    private val data: String,
    val dataFile: File = File(data).canonicalFile,

    val thumbFile: File = File("$data/thumb").canonicalFile,
    val unsupportedFile: File = File("$data/unsupported").canonicalFile,

    val titleDigits: Int = 4,
    val volumeDigits: Int = 5,
)
