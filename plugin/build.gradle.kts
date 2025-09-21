plugins {
    id("java")
    id("io.freefair.lombok") version "9.0.0-rc2"
    id("com.gradleup.shadow") version "9.1.0"
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation(project(":api"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.processResources {
    filesMatching("paper-plugin.yml") {
        expand(mapOf("version" to rootProject.version))
    }
}

