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

dependencies {
    // development dependecies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	implementation("commons-codec:commons-codec:$commonsCodecVersion")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.codehaus.groovy:groovy-all:$groovyVersion")
    testImplementation(platform("org.spockframework:spock-bom:$spockBomVersion"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-spring")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    integrationImplementation("com.icegreen:greenmail-spring:$greenmailVersion")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
