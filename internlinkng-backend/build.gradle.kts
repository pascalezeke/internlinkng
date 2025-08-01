plugins {
    kotlin("jvm") version "2.0.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-gson:2.3.7")
    implementation("io.ktor:ktor-server-cors:2.3.7")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("org.jetbrains.exposed:exposed-core:0.45.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.45.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.7")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.7")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.internlinkng.backend.ApplicationKt")
}

kotlin {
    jvmToolchain(17)
} 