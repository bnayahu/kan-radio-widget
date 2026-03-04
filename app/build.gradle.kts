plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jbworks.radiowidget"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jbworks.radiowidget"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("com.google.android.material:material:1.11.0")

    // Media3/ExoPlayer for HLS streaming
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.1")
    implementation("androidx.media3:media3-session:1.2.1")

    // WorkManager (optional backup updates)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")

    // RecyclerView for config activity
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Media for notification style
    implementation("androidx.media:media:1.7.0")
}
