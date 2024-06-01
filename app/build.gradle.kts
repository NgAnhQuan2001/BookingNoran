plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.aqsoft.bookingappnoran"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aqsoft.bookingappnoran"
        minSdk = 24
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


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation ("com.google.firebase:firebase-database:20.3.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    // Import Firebase Storage
    implementation ("com.google.mlkit:face-detection:16.1.6")

    //camera
    implementation ("androidx.camera:camera-core:1.2.0-alpha04")
    implementation ("androidx.camera:camera-camera2:1.2.0-alpha04")
    implementation ("androidx.camera:camera-lifecycle:1.2.0-alpha04")
    implementation ("androidx.camera:camera-view:1.2.0-alpha04")

    //import gson
    implementation ("com.google.code.gson:gson:2.8.8")
    // //TensorFlow Lite libraries (To recognize faces)
    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.3.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.3.0")
    implementation ("org.tensorflow:tensorflow-lite:0.0.0-nightly-SNAPSHOT")

    implementation(fileTree(mapOf("dir" to "C:\\Users\\Anh Quan\\Downloads\\Compressed\\DemoZPDK_Android_2\\DemoZPDK_Android\\ZPDK-Android", "includes" to listOf("*.aar", "*.jar"))))
    implementation ("com.squareup.okhttp3:okhttp:4.6.0")

}