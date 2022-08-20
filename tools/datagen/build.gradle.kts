plugins {
    java
    application
}

dependencies {
    implementation("com.github.erosb:everit-json-schema:1.14.1")
}

application {
    mainClass.set("unnamed.mmo.datagen.Main")
}
