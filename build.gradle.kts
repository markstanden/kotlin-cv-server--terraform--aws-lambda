import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val dotenv_version: String by project
val junit_version: String by project
val logback_version: String by project

plugins {
	kotlin("jvm") version "1.7.10"
	id("com.github.johnrengelman.shadow") version "5.1.0"
	kotlin("plugin.serialization") version "1.7.10"
}

group = "dev.markstanden"
version = "1.0-SNAPSHOT"



repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib:1.7.10"))
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
	implementation("com.amazonaws:aws-lambda-java-events:3.11.0")
	testImplementation(kotlin("test"))
	implementation("io.github.cdimascio:dotenv-kotlin:$dotenv_version")
}

tasks.shadowJar {
	archiveBaseName.set("shadow")
	archiveClassifier.set("")
	archiveVersion.set("")
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "11"
}