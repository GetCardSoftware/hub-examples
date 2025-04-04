plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("androidx.room")
}

android {

    namespace = "com.getcard.completeposexample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.getcard.completeposexample"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "device"

    productFlavors {
        create("gpos720") {
            dimension = "device"
        }
        create("gpos760") {
            dimension = "device"
        }
        create("gpos780") {
            dimension = "device"
            ndk {
                abiFilters.add("armeabi-v7a")
            }
        }
    }

    sourceSets {
        create("gpos720Debug") {}
        create("gpos720Release") {}
        create("gpos760Debug") {}
        create("gpos760Release") {}
        create("gpos780Debug") {}
        create("gpos780Release") {}
    }

    signingConfigs {
        create("gertecDevelopment") {
            storeFile = file("../key/Development_GertecDeveloper_EnhancedAPP.jks")
            storePassword = "Development@GertecDeveloper2018"
            keyAlias = "developmentgertecdeveloper_enhancedapp"
            keyPassword = "Development@GertecDeveloper2018"
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

    room {
        schemaDirectory("$projectDir/schemas")
    }

}


dependencies {

    ////Implementação do Room para manipular banco de dados SQLite//////////////////

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")
//////////////////////////////////////////////////////////////////////

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

    // Hub
    "gpos720DebugImplementation"(libs.hub.sitef.provider.gpos720.debug)
    "gpos720ReleaseImplementation"(libs.hub.sitef.provider.gpos720.release)

    "gpos760DebugImplementation"(libs.hub.sitef.provider.gpos760.debug)
    "gpos760ReleaseImplementation"(libs.hub.sitef.provider.gpos760.release)

    "gpos780DebugImplementation"(libs.hub.sitef.provider.gpos780.debug)
    "gpos780ReleaseImplementation"(libs.hub.sitef.provider.gpos780.release)
}