import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
	kotlin("jvm") version "1.7.10"
	id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "dev.markstanden"
version = "1.0-SNAPSHOT"



repositories {
	mavenCentral()
	maven("https://jitpack.io")
}

dependencies {
	implementation(kotlin("stdlib:1.7.10"))
	implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
	implementation("com.amazonaws:aws-lambda-java-events:3.6.0")
	implementation("com.amazonaws:aws-lambda-java-tests:1.1.1")
	implementation("com.amazonaws:aws-java-sdk-bom:1.12.239")
	implementation("com.amazonaws:aws-lambda-java-events-sdk-transformer:3.1.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
	testImplementation(kotlin("test"))
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
	kotlinOptions.jvmTarget = "1.8"
}