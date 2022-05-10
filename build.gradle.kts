plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    java
}

group = "com.daimler.traceability"
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

val swaggerAnnotationsVersion = "1.6.6"
val jsr305Version = "3.0.2"
val mapstructVersion = "1.4.2.Final"
val lombokMapstructBindingVersion = "0.2.0"

dependencies {
//    development/test dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("commons-codec:commons-codec:1.15")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

// discuss if we want remove some of the dependencies below
    implementation("io.swagger:swagger-annotations:$swaggerAnnotationsVersion")
    implementation("com.google.code.findbugs:jsr305:$jsr305Version")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")


}

tasks {
    test {
        useJUnitPlatform()
    }
}
