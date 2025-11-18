import org.gradle.api.JavaVersion

plugins {
    id("com.android.application") version "8.13.1"
    kotlin("android") version "1.9.10"
}

android {
    namespace = "com.safetransfer.ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.safetransfer.ai"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    // Versión de Compose Compiler compatible con Kotlin 1.9.10
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
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
    // Jetpack Compose UI
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.0")

    // Necesarios para KeyboardOptions, TextField, etc.
    implementation("androidx.compose.ui:ui-text:1.7.0")
    implementation("androidx.compose.foundation:foundation:1.7.0")

    // Activity Compose (para ComponentActivity y setContent)
    implementation("androidx.activity:activity-compose:1.9.3")

    // ViewModel (por si lo usamos luego)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    // Room (runtime por ahora)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Location Services
    implementation("com.google.android.gms:play-services-location:21.2.0")
}

