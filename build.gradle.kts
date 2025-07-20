plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    
    // Coroutines for async patterns
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    
    // Serialization for examples
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    
    // Date/Time library
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    
    // Reflection for advanced patterns
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("io.mockk:mockk:1.13.12")
    
    // Benchmarking
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.11")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}

application {
    mainClass.set("MainKt")
}