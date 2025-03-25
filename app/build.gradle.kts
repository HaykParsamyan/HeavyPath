plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.heavypath_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.heavypath_project"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        resources.excludes += setOf("META-INF/NOTICE.md", "META-INF/LICENSE.md")
    }
}

dependencies {
    // Firebase BOM for managing Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))

    // Firebase modules
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // AndroidX RecyclerView for better performance
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Android Material Components for UI
    implementation("com.google.android.material:material:1.9.0")

    // AndroidX ConstraintLayout for responsive design
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle components for MVVM architecture
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // Email library dependencies
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    // CircleImageView for circular profile images
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}