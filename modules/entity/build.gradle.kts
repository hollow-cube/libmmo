plugins {
    `java-library`
}

dependencies {
    implementation(project(":modules:common"))
    implementation(files("libs/enodia-pf-1.0.1.jar"))
}
