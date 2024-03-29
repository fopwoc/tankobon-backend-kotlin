val javaVersion: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val exposedVersion: String by project
val detektVersion: String by project
val hopliteVersion: String by project
val logbackVersion: String by project

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    }
}

group = "com.tankobon"
version = "0.0.1"
description = "Server for Tankōbon - a flutter manga reader app. WIP."
application {
    mainClass.set("com.tankobon.ApplicationKt")
}

plugins {
    application
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("com.github.johnrengelman.shadow")
    id("io.gitlab.arturbosch.detekt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("com.lordcodes.turtle:turtle:0.8.0")

    implementation("com.sksamuel.scrimage:scrimage-core:4.0.36")

    implementation("ch.qos.logback:logback-core:$logbackVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.slf4j:slf4j-api:2.0.7")

    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")

    testImplementation(kotlin("test"))

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }
    shadowJar {
        isZip64 = true
        mergeServiceFiles()
    }
}

tasks.test {
    useJUnitPlatform()
}
