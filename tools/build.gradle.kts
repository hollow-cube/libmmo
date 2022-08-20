import net.ltgt.gradle.errorprone.errorprone

subprojects {
    apply(plugin = "java")
    apply(plugin = "net.ltgt.errorprone")

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
        val testAnnotationProcessor by configurations
        val errorprone by configurations

        errorprone("com.google.errorprone:error_prone_core:2.14.0")
        errorprone("com.uber.nullaway:nullaway:0.9.8")

        // Auto service (SPI)
        annotationProcessor("com.google.auto.service:auto-service:1.0.1")
        testAnnotationProcessor("com.google.auto.service:auto-service:1.0.1")
        implementation("com.google.auto.service:auto-service-annotations:1.0.1")

        // Testing
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
        testImplementation("com.google.truth:truth:1.1.3")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.errorprone.disableWarningsInGeneratedCode.set(true)
        options.errorprone {
            check("NullAway", net.ltgt.gradle.errorprone.CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "com.uber")
        }
    }
}
