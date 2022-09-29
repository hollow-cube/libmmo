var ext = (gradle as ExtensionAware).extra

rootProject.name = "libmmo"

val env = mutableMapOf<String, String>()
file(".env").readLines().forEach {
    //todo add regular env vars, just use this as a fallback.
    if (it.isNotEmpty() && !it.startsWith("#")) {
        val pos = it.indexOf("=")
        val key = it.substring(0, pos)
        var value = it.substring(pos + 1)
        if (System.getenv(key) != null)
            value = System.getenv(key)
        env[key] = value
    }
}

val commonPath = env["COMMON_LIB_PATH"]
if (commonPath != null) {
    includeBuild(commonPath)
} else {
    System.err.println("COMMON_LIB_PATH not set, using maven dependency instead. NOT CURRENTLY IMPLEMENTED")
}

include(":modules")
include(":modules:common")
include(":modules:chat")
include(":modules:item")
include(":modules:block-interactions")
include(":modules:loot-table")
include(":modules:player")
include("modules:quest")
include(":modules:development")
