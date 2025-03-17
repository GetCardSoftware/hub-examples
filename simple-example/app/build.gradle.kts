plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {

    namespace = "com.getcard.simpleexample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.getcard.simpleexample"
        minSdk = 26
        lint.targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    signingConfigs {
        create("gertecDevelopment") {
            storeFile = file("../key/GPOS700/swex-gpos700-devel.jks")
            storePassword = "swpos123456"
            keyPassword = "SWex123"
            keyAlias = "enhanced"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("gertecDevelopment")
        }
        debug {
            signingConfig = signingConfigs.getByName("gertecDevelopment")
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

    implementation(libs.androidx.preference)

    implementation(libs.rxandroid)
    implementation(libs.signalr)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.squareup.retrofir2.retrofit)
    implementation(libs.squareup.retrofir2.converter.gson)
    implementation(libs.squareup.retrofir2.adapter.rxjava3)

    implementation(libs.squareup.okhttp3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Payment hub
    implementation(libs.hub.sitef.provider)
 }