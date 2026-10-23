plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")

}

android {
    namespace = "com.aceapps.main"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    api("com.revenuecat.purchases:purchases:9.19.1")

    api(libs.converter.gson)
    api(libs.gson)
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    //room db
    implementation("androidx.room:room-runtime:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    api("com.facebook.android:facebook-android-sdk:18.0.3")
    api("com.android.installreferrer:installreferrer:2.2")
    api("de.hdodenhof:circleimageview:3.1.0")
    api("com.google.android.play:review:2.0.1")


    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))

    // Firebase
    implementation(libs.firebase.database)
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-crashlytics")

    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")


}