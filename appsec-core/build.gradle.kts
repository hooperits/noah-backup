plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
}

dependencies {
    // Spring Boot Security and Web
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    
    // Rate limiting and throttling
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.springframework.boot:spring-boot-starter-data-redis") {
        exclude(group = "redis.clients", module = "jedis")
    }
    implementation("io.lettuce:lettuce-core")
    
    // Input validation and sanitization
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.owasp.encoder:encoder:1.2.3")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    
    // Security monitoring and metrics
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    
    // JSON processing for security configs
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Integration with other Noah Backup modules
    implementation(project(":auth-core"))
    
    // JWT for security token validation
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    
    // Cryptography for security features
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}