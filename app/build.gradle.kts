plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.sak.weatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sak.weatherapp"
        minSdk = 30
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
     // Replace this with the correct version if needed
    implementation("com.airbnb.android:lottie:6.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-location:17.0.0")
// GSON converter
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
// retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.android.volley:volley:1.2.1")
}
