import org.screamingsandals.gradle.builder.*

plugins {
    java
    application
    kotlin("jvm") version libs.versions.kotlin.get()
    alias(libs.plugins.screaming.plugin.builder)
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
    maven("https://maven.reposilite.com/releases/")
}

application {
    mainClass.set("org.screamingsandals.reposilite.webhook.WebhookPlugin")
}

dependencies {
    compileOnly(libs.reposilite)
}

configureLicenser()
configureJavac(JavaVersion.VERSION_11)
configureSourcesJar()
setupMavenPublishing(addSourceJar=true)
setupMavenRepositoriesFromProperties()
