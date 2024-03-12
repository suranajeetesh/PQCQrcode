plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.pqc.qr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pqc.qr"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")

    // ViewModel and LiveData
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    implementation("com.github.bumptech.glide:glide:4.15.1")
    ksp("com.github.bumptech.glide:compiler:4.15.1")

    /* Gson */
//    implementation 'com.google.code.gson:gson:2.8.8'

    /* Retrofit */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation "com.squareup.retrofit2:converter-gson:2.9.0"
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.6.1")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    /* moshi */
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.9.3")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    /* Retrofit coroutines */
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    //AdMob
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    //preference
    implementation("androidx.preference:preference-ktx:1.2.1")






}