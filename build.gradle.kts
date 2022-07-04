plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.light.MainKt"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.twasi:obs-websocket-java:1.2.0")
    implementation("org.slf4j:slf4j-api:1.6.1")
    implementation("org.slf4j:slf4j-simple:1.6.1")
    implementation("org.hid4java:hid4java:0.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.3")
}