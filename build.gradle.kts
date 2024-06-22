plugins {
    `java-library`
    `kotlin-dsl`
    signing
}

buildscript {
    repositories {
        mavenCentral()
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.get()))
}

apply {
    fileTree(rootProject.file("./gradle/internal")) {
        include("*.gradle")
    }.forEach(::from)
}
