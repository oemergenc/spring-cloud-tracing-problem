plugins {
	java
	id("org.springframework.boot") version "2.7.9"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
}

group = "com.example.tracing.problem"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.9"))
	implementation(platform("com.google.cloud:spring-cloud-gcp-dependencies:3.4.5"))
	implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2021.0.6"))

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.google.cloud:spring-cloud-gcp-logging")
	implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
	implementation("io.projectreactor.netty:reactor-netty-http-brave")
	implementation("io.zipkin.gcp:brave-propagation-stackdriver:1.0.4")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("net.javacrumbs.json-unit:json-unit-fluent:2.36.1")
	testImplementation("org.awaitility:awaitility:4.0.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
