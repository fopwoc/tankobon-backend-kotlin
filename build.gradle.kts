tasks.wrapper {
    gradleVersion = "7.5.1"
    distributionType = Wrapper.DistributionType.BIN
}

group = "com.tankobon"
version = "0.0.1"
description = "Server for Tankōbon - a flutter manga reader app. WIP."
application {
    mainClass.set("com.tankobon.ApplicationKt")
}

plugins {
    application
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.1.1")
    implementation("io.ktor:ktor-server-host-common-jvm:2.1.1")
    implementation("io.ktor:ktor-server-netty-jvm:2.1.1")
    implementation("io.ktor:ktor-server-auth-jvm:2.1.3")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.1.1")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.1.1")
    implementation("io.ktor:ktor-server-status-pages:2.1.1")
    implementation("io.ktor:ktor-server-call-logging:2.1.1")
    implementation("io.ktor:ktor-server-content-negotiation:2.1.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.1")
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.jetbrains.exposed:exposed-core:0.39.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.39.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.39.2")

    implementation("com.h2database:h2:2.1.214")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    implementation("com.github.junrar:junrar:7.5.3")
    implementation("net.lingala.zip4j:zip4j:2.11.2")

    implementation("com.sksamuel.scrimage:scrimage-core:4.0.32")

    implementation("ch.qos.logback:logback-classic:1.4.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
}

val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))
