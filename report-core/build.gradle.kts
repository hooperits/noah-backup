plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.3"
}

dependencies {
    // JSON processing for reports
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Spring Boot for configuration and scheduling integration
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    
    // Integration with other Noah Backup modules
    implementation(project(":auth-core"))
    implementation(project(":storage-s3"))
    
    // Email/notification support (future)
    implementation("org.springframework.boot:spring-boot-starter-mail")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}