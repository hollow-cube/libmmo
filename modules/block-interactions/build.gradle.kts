plugins {
    `java-library`
}

dependencies {
    implementation("net.hollowcube.common:common:0.0.0")
    implementation(project(":modules:item"))
    implementation(project(":modules:loot-table"))
    implementation(project(":modules:player"))

    compileOnly(project(":modules:quest"))
}