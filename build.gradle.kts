plugins {
    `java-library`
}

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    }
}

group = "com.venomvendor.library"
version = "1.0.0"

repositories {
    jcenter()
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

apply {
    from("dependencies.gradle.kts")
    from("publish.gradle")
    from("jacoco.gradle")
}
