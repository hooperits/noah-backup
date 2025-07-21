plugins {
    java
}

dependencies {
    // For encryption and security
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    
    // Integration with other Noah Backup modules
    implementation(project(":storage-s3"))
}