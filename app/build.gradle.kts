import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.firebase)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "com.goskar.boardgame"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.goskar.boardgame"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.1.1"

        val localProperties  = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        val apiKey = localProperties.getProperty("BGG_API_KEY") ?: ""

        buildConfigField("String", "BGG_API_KEY", "\"$apiKey\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    //noinspection UseTomlInstead
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.timber)
    implementation (libs.core)
    implementation (libs.calendar)

    //charts
    implementation (libs.composecharts)

    implementation(libs.bundles.camera.all)

    implementation(libs.bundles.voyager.all)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase.all)
    implementation(libs.bundles.koin.all)
    implementation(libs.bundles.retrofit.gson)
    implementation(libs.bundles.room.all)
    implementation(libs.bundles.coil.all)

    // SimpleXML for parsing the BGG XML API.
    // Exclude xpp3 / stax: they bundle a duplicate org.xmlpull.v1.XmlPullParser
    // that the Android platform already provides, which breaks R8 (release minify).
    implementation(libs.converter.simplexml) {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "stax", module = "stax")
        exclude(group = "xpp3", module = "xpp3")
    }
    implementation(libs.simple.xml) {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "stax", module = "stax")
        exclude(group = "xpp3", module = "xpp3")
    }

    ksp(libs.room.ksp)
}