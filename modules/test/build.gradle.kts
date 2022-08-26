
plugins {
    `java-library`
}

dependencies {

    // JUnit
    api("org.junit.jupiter:junit-jupiter-api:5.9.0")
    api("org.junit.jupiter:junit-jupiter-params:5.9.0")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    // Truth
    api("com.google.truth:truth:1.1.3")


    // TestContainers
    fun testContainersApi(name: String) {
        api("org.testcontainers:$name:1.17.3") {
            exclude(group = "junit", module = "junit")
        }
    }

    testContainersApi("testcontainers")
    testContainersApi("junit-jupiter")
    testContainersApi("mongodb")


}