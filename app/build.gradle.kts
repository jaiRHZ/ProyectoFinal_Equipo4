plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "rodriguez.jairo.proyectofinal_reviewssystem"
    compileSdk = 35

    defaultConfig {
        applicationId = "rodriguez.jairo.proyectofinal_reviewssystem"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
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
        viewBinding = true
    }
}

dependencies {
    //Firebase Firestore
    implementation ("com.google.firebase:firebase-firestore-ktx:25.1.3")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    //ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")

    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-auth")


    // All:
    implementation ("com.cloudinary:cloudinary-android:3.0.2")

// Download + Preprocess:
    implementation ("com.cloudinary:cloudinary-android-download:3.0.2")
    implementation ("com.cloudinary:cloudinary-android-preprocess:3.0.2")

    implementation ("com.github.bumptech.glide:glide:4.16.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}