import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
	implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
	implementation("com.amazonaws:aws-lambda-java-events:3.11.0")
//	implementation("com.amazonaws:aws-lambda-java-events:3.6.0")
//	implementation("com.amazonaws:aws-lambda-java-tests:1.1.1")
//	implementation("com.amazonaws:aws-java-sdk-bom:1.12.239")
//	implementation("com.amazonaws:aws-lambda-java-events-sdk-transformer:3.1.0")
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
	kotlinOptions.jvmTarget = "11"
}