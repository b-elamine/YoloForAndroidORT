plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.yoloonnx"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.yoloonnx"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
val cameraxVersion = "1.1.0-alpha10"


dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.16.0-rc1")
    // CameraX core library
    // CameraX core library
    implementation("androidx.camera:camera-core:$cameraxVersion")
    // CameraX camera2 implementation
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    // CameraX lifecycle library
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    // CameraX view binding library
    implementation("androidx.camera:camera-view:1.0.0-alpha30")
}