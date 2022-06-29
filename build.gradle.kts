plugins {
	id("java")
	id("groovy")
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.autonomousapps.dependency-analysis") version "1.2.0"
	id("com.google.cloud.tools.jib") version "3.2.1"
	id("com.coditory.integration-test") version "1.4.0"
}

group = "net.catenax.traceability"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

configurations {
	compileOnly.get().extendsFrom(configurations["annotationProcessor"])
}

repositories {
	maven { url = uri("https://repo.spring.io/release") }
	mavenCentral()
}

val commonsCodecVersion = "1.15"
val groovyVersion = "3.0.10"
val spockBomVersion = "2.1-groovy-3.0"
val greenmailVersion = "1.6.9"
val springfoxVersion = "3.0.0"
val keycloakVersion = "18.0.0"

dependencies {
    // development dependecies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

	implementation("org.springframework.data:spring-data-commons")

	implementation("org.keycloak:keycloak-spring-boot-starter:$keycloakVersion")

	implementation("io.springfox:springfox-boot-starter:$springfoxVersion")

	implementation("org.springframework.data:spring-data-commons")

	implementation("io.springfox:springfox-boot-starter:$springfoxVersion")

	// for demo purposes, to be removed once EDC works
	implementation("com.github.javafaker:javafaker:1.0.2") {
		exclude("org.yaml")
	}

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	implementation("commons-codec:commons-codec:$commonsCodecVersion")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.codehaus.groovy:groovy-all:$groovyVersion")
    testImplementation(platform("org.spockframework:spock-bom:$spockBomVersion"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-spring")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

    integrationImplementation("com.icegreen:greenmail-spring:$greenmailVersion")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
