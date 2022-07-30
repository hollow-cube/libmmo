
dependencies {
    implementation("org.mongodb:mongodb-driver-sync:4.7.0")
    testImplementation("org.testcontainers:mongodb:1.17.3") {
        exclude(group = "junit", module = "junit") // JUnit 4
    }

    testImplementation("com.google.truth:truth:1.1.3")
}

tasks.test {
    if (project.hasProperty("excludeTests")) {
        filter {
            excludeTestsMatching(project.property("excludeTests") as String)
        }
    }
}
