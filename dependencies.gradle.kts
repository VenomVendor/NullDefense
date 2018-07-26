val implementation by configurations
val testImplementation by configurations

dependencies {
    implementation("com.google.code.gson:gson:2.8.5")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.2.0")
    testImplementation("org.junit.platform:junit-platform-runner:1.2.0")
}
