
plugins {
    `java-library`
}

repositories {
//    maven(url = "https://libraries.minecraft.net")
}

dependencies {
    api(project(":dfu"))
//    implementation("com.mojang:datafixerupper:1.0.20")
}
