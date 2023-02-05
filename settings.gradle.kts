rootProject.name = "tankobon-server-kotlin"

pluginManagement {
    val kotlinVersion: String by settings
    val detektVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("com.github.johnrengelman.shadow") version "7.1.2"
        id("io.gitlab.arturbosch.detekt") version detektVersion
    }
}
