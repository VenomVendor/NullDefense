extra["major"] = 2
extra["minor"] = 0
extra["patch"] = 0

val suffix = ""
val version = "${extra["major"]}.${extra["minor"]}.${extra["patch"]}"

extra["currentVersion"] = "$version$suffix"
extra["group"] = "com.venomvendor"
