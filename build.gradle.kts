// Top‑level build file
plugins {
    // DON’T apply – only declare
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.kotlin.android)           apply false
}

// extra build‑script class‑paths (old‑style) ---------------------------
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Google services (Firebase)
        classpath("com.google.gms:google-services:4.3.15")

        // **OneSignal Gradle plugin** (required)
        classpath("gradle.plugin.com.onesignal:onesignal-gradle-plugin:0.14.0")
    }
}