plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.0-M1"
}

group = "org.laolittle.plugin"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    val exposedVersion = "0.36.2"
    val ktorVersion = "1.6.7"
    implementation("org.xerial:sqlite-jdbc:3.36.0.2")
    implementation("com.alibaba:druid:1.2.8")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("org.quartz-scheduler:quartz:2.3.2")
}