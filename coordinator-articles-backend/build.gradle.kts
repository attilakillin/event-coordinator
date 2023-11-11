import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
	kotlin("plugin.jpa") version "1.7.22"
}

group = "hu.attilakillin"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation ("org.jsoup:jsoup:1.15.4")

	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core")
	}
	testImplementation("io.mockk:mockk:1.12.0")
	testImplementation("com.ninja-squad:springmockk:4.0.2")
	testImplementation("com.h2database:h2:1.3.148")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<BootJar> {
	archiveFileName.set(archiveBaseName.get() + "." + archiveExtension.get())
	destinationDirectory.set(file(project.buildDir))
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sourceSets {
	create("test-integration") {
		kotlin {
			compileClasspath += main.get().output + configurations.testRuntimeClasspath
			runtimeClasspath += output + compileClasspath
		}
	}
}

val testIntegration = task<Test>("testIntegration") {
	group = "verification"

	testClassesDirs = sourceSets["test-integration"].output.classesDirs
	classpath = sourceSets["test-integration"].runtimeClasspath
	shouldRunAfter(tasks["test"])
}

tasks.check {
	dependsOn(testIntegration)
}
