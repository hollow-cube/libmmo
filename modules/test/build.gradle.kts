
plugins {
    `java-library`
}

dependencies {

    // JUnit
    api("org.junit.jupiter:junit-jupiter-api:5.8.1")
    api("org.junit.jupiter:junit-jupiter-params:5.8.1")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

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