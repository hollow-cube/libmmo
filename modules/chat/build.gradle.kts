
dependencies {
    implementation(project(":modules:common"))

    implementation("org.mongodb:mongodb-driver-sync:4.7.0")
}

tasks.test {
    if (project.hasProperty("excludeTests")) {
        filter {
            excludeTestsMatching(project.property("excludeTests") as String)
        }
    }
}
