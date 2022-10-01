plugins {
    application
}

dependencies {
    implementation("com.github.hollow-cube:Minestom:e84114b752")
    implementation(project(":modules:common"))
    implementation(project(":modules:chat"))
    implementation(project(":modules:block-interactions"))
    implementation(project(":modules:item"))
    implementation(project(":modules:player"))
    implementation(project(":modules:quest"))

    implementation("org.mongodb:mongodb-driver-sync:4.7.1")
}

application {
    mainClass.set("net.hollowcube.server.dev.Main")
}

tasks.named("run", JavaExec::class) {
    workingDir("build")
}
