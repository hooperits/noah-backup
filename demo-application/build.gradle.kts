plugins {
    java
    application
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.4"
}

dependencies {
    // All Noah Backup modules
    implementation(project(":filesystem-windows"))
    implementation(project(":storage-s3"))
    implementation(project(":scheduler-core"))
    
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // CLI support
    implementation("org.springframework.shell:spring-shell-starter:3.1.5")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
    mainClass.set("com.noahbackup.demo.NoahBackupDemoApplication")
}