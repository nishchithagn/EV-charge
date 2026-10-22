// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 👇 Using explicit string declarations to completely bypass Version Catalog bugs
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false

    // 👇 Firebase Google Services Plugin Infrastructure
    id("com.google.gms.google-services") version "4.4.2" apply false
}
