import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar as ShadowJar


plugins {
    java
    application
    id("io.freefair.lombok") version "6.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "network.frostless"
version = "0.0.1"


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

var log4jVersion = "2.17.1"

repositories {
    mavenCentral()

    maven("https://m2.dv8tion.net/releases")
}

application {
    mainClass.set("network.frostless.mist.Application")
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

// Configure Shadow to output with normal jar file name:
tasks.named<ShadowJar>("shadowJar").configure {
    minimize()
    archiveFileName.set("${project.rootProject.name}-${project.name}-v${project.version}.jar")
    destinationDirectory.set(file("$rootDir/output"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
