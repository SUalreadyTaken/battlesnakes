plugins {
    java
    id("org.springframework.boot") version "2.1.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

group = "com.battle.snakes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-test")
        compileOnly("org.springframework.boot:spring-boot-configuration-processor")
        compileOnly("org.projectlombok:lombok:1.18.4")
        compileOnly("org.slf4j:log4j-over-slf4j:1.7.25")
        annotationProcessor("org.projectlombok:lombok:1.18.4")

        testImplementation("org.assertj:assertj-core")
        testImplementation("org.mockito:mockito-core:2.24.0")
        testImplementation("org.mockito:mockito-junit-jupiter:2.24.0")
        testImplementation("org.junit.platform:junit-platform-runner:1.3.2")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.2")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")

        tasks.test {
            useJUnitPlatform()
        }
    }

}

tasks.wrapper {
    version = "5.2.1"
}
