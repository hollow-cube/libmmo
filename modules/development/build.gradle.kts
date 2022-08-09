
plugins {
    application
}

dependencies {
    implementation(project(":modules:common"))
    implementation(project(":modules:chat"))
    implementation(project(":modules:block-interactions"))

    implementation("org.mongodb:mongodb-driver-sync:4.7.0")
}

application {
    mainClass.set("unnamed.mmo.server.dev.Main")
}
