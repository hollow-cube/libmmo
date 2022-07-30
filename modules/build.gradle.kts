
subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }


    dependencies {
        // A bug with kotlin dsl
        val implementation by configurations
        val annotationProcessor by configurations
        val testImplementation by configurations
        val testRuntimeOnly by configurations

        // Auto service (SPI)
        annotationProcessor("com.google.auto.service:auto-service:1.0.1")
        implementation("com.google.auto.service:auto-service-annotations:1.0.1")

        // Minestom
        implementation("com.github.Minestom:Minestom:d596992c0eafd8c")

        // Testing
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

        testImplementation("org.testcontainers:testcontainers:1.17.3") {
            exclude(group = "junit", module = "junit")
        }
        testImplementation("org.testcontainers:junit-jupiter:1.17.3") {
            exclude(group = "junit", module = "junit")
        }
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }
}
