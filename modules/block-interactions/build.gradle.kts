plugins {
    `java-library`
}

dependencies {
    implementation(project(":modules:common"))
    implementation(project(":modules:item"))
    implementation(project(":modules:loot-table"))
    implementation(project(":modules:player"))

    compileOnly(project(":modules:quest"))
}