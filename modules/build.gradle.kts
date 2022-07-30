
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

        // Auto service (SPI)
        annotationProcessor("com.google.auto.service:auto-service:1.0.1")
        implementation("com.google.auto.service:auto-service-annotations:1.0.1")

        // Minestom
        implementation("com.github.Minestom:Minestom:d596992c0eafd8c")

        // Testing
        testImplementation(project(":modules:test"))
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }
}
