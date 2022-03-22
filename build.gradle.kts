plugins {
    java
    id("io.freefair.lombok") version "6.3.0"
}

group = "network.frostless"
version = "0.0.1"


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

var log4jVersion = "2.17.1"

repositories {
    mavenCentral()

    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    // Log4j
    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-iostreams:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-jul:${log4jVersion}")
    implementation("net.minecrell:terminalconsoleappender:1.3.0")
    runtimeOnly ("com.lmax:disruptor:3.4.4") // Async loggers


    // Google
    implementation("com.google.guava:guava:31.1-jre")

    // JDA
    implementation("net.dv8tion:JDA:4.4.0_350")

    // Hikari
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.3.3")

    // FrostCore
    implementation("network.frostless:frostcore:0.0.1")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("io.lettuce:lettuce-core:6.1.6.RELEASE")
    // ORM
    implementation("com.j256.ormlite:ormlite-core:6.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")


    // Server API
    implementation("network.frostless:ServerAPI:0.0.1")

    // LogSnag
    implementation("cc.ricecx:logsnag4j:1.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}