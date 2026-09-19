plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "peugeot.platform.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "peugeot.vehicle.os"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "31.0.0"

        buildConfigField(
            "String",
            "OPENAI_API_KEY",
            "\"${System.getenv("OPENAI_API_KEY") ?: ""}\""
        )
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}


dependencies {

    implementation(
        "com.squareup.okhttp3:okhttp:4.12.0"
    )

    implementation(
        "com.google.code.gson:gson:2.11.0"
    )

}
