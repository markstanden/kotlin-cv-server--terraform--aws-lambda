import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.7.10"
	id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "dev.markstanden"
version = "1.0-SNAPSHOT"



repositories {
	mavenCentral()
}

dependencies {
	implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
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