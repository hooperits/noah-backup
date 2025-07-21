plugins {
    java
    kotlin("jvm") version "1.9.10" apply false
}

allprojects {
    group = "com.noahbackup"
    version = "1.0.0"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
    
    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.9")
        implementation("ch.qos.logback:logback-classic:1.4.11")
        testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
    
    tasks.test {
        useJUnitPlatform()
    }
}