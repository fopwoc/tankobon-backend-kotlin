val kotlinVersion: String by project
val ktorVersion: String by project
val detektVersion: String by project

tasks.wrapper {
    gradleVersion = "7.6"
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
    kotlin("jvm")
    kotlin("plugin.serialization")
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
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    implementation("com.h2database:h2:2.1.214")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("com.lordcodes.turtle:turtle:0.8.0")

    implementation("com.sksamuel.scrimage:scrimage-core:4.0.33")

    implementation("ch.qos.logback:logback-core:1.4.5")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.slf4j:slf4j-api:2.0.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

