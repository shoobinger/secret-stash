import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.21"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.2.0"
    kotlin("plugin.jpa") version "2.2.0"
    id("org.openapi.generator") version "7.14.0"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
}

group = "me.ivansuvorov"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    runtimeOnly("org.postgresql:postgresql:42.7.7")
    runtimeOnly("com.zaxxer:HikariCP:5.0.1")
    implementation("org.flywaydb:flyway-database-postgresql:11.10.1")

    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.nimbusds:nimbus-jose-jwt:10.2")
    implementation("com.google.crypto.tink:tink:1.7.0") // For Ed25519.

    implementation("io.github.resilience4j:resilience4j-ratelimiter:2.2.0")

    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<KotlinCompile> {

    dependsOn("openApiGenerate")
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec = "$rootDir/secret-stash.openapi.yaml"
    outputDir.set("$buildDir/generated")
    modelPackage = "com.ivansuvorov.secretstash.api.model"
    configOptions.set(
        mapOf(
            "serializationLibrary" to "jackson",
        ),
    )
    typeMappings.set(
        mapOf(
            "DateTime" to "Instant",
        ),
    )
    importMappings.set(
        mapOf(
            "Instant" to "java.time.Instant",
        ),
    )
    globalProperties.set(
        mapOf(
            "models" to "",
            "modelDocs" to "false",
            "modelTests" to "false",
            "apis" to "false",
            "apiDocs" to "false",
            "apiTests" to "false",
        ),
    )
}

sourceSets["main"].java.srcDir("$buildDir/generated/src/main/kotlin")
