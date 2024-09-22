// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Application plugin
    alias(libs.plugins.android.application) apply false

    // Google Maps Secrets plugin
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false

    // Google Services plugin for Firebase
    id("com.google.gms.google-services") version "4.3.15" apply false // Update to the latest version if necessary
}
