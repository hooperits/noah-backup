plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.4"
}

dependencies {
    // Spring Boot for scheduling
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    
    // Integration with other modules
    implementation(project(":filesystem-windows"))
    implementation(project(":storage-s3"))
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}