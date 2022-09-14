plugins {
    `java-library`
}

dependencies {
    api("com.github.minestommmo:DataFixerUpper:cf58e926a6")
    api("net.kyori:adventure-text-minimessage:4.11.0")

    implementation("org.tinylog:tinylog-impl:2.4.1")

    implementation("io.github.cdimascio:dotenv-java:2.2.4")

    // Optional components
    compileOnly("org.mongodb:mongodb-driver-sync:4.7.0")


}
