rootProject.name = "tankobon-server-kotlin"

pluginManagement {
    val kotlinVersion: String by settings
    val detektVersion: String by settings
    val ktorVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
        id("com.github.johnrengelman.shadow") version "8.0.0"
        id("io.gitlab.arturbosch.detekt") version detektVersion
    }
}
