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

    api(platform("com.google.firebase:firebase-bom:33.2.0")) //firebase bom
    api(libs.firebase.firestore) //for firestore
    api(libs.firebase.auth) ///for auth
    api("com.google.firebase:firebase-crashlytics") //for crashlytics
    api("com.google.firebase:firebase-config") //for remote config
    api("com.google.firebase:firebase-analytics") //for analytics
    api ("com.google.android.gms:play-services-auth:21.2.0") // google auth
    api ("com.google.firebase:firebase-messaging-ktx") // google auth
    //room db
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    api("com.google.firebase:firebase-auth-ktx")
    api("com.facebook.android:facebook-android-sdk:18.0.3")
    api("com.android.installreferrer:installreferrer:2.2")
    api("de.hdodenhof:circleimageview:3.1.0")
    api("com.google.android.play:review:2.0.1")
}