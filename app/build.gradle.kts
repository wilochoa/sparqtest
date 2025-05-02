plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")

}

hilt {
    enableAggregatingTask = false
}

android {
    namespace = "com.sensate.sparqtest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sensate.sparqtest"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true  // Enable BuildConfig generation
    }
}

dependencies {

    // Other modules
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":domain"))

    // Hilt (DI entry point)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}