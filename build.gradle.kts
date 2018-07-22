plugins {
    `java-library`
    `kotlin-dsl`
}

rootProject.apply {
    from("version.gradle.kts")
}

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
        classpath("org.kt3k.gradle.plugin:coveralls-gradle-plugin:2.8.2")
    }
}

group = extra["group"] as String
version = extra["currentVersion"] as String

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
