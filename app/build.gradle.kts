plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.0"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.baselineprofile)
    id("ru.ok.tracer").version("1.0.1")
}

tracer {
    create("defaultConfig") {
        pluginToken = "562suLzs2fpFm6K28WZYqYUR0tJRWE39l1RNNOpdiyv1"
        appToken = "cIyaFR772N1jvb5ltIKBukXI9XFuOwwvzz00tSGKwmr1"

        // Включает загрузку маппингов для билда. По умолчанию включена
        uploadMapping = true
    }

}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/dimitrisimonyan/Yandex.Disk.localized/Проекты/Android/signkey")
            storePassword = "vd410078060"
            keyAlias = "application"
            keyPassword = "vd410078060"
        }
    }
    namespace = "com.mycollege.schedule"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mycollege.schedule"
        minSdk = 29
        versionCode = 3
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true //R8 compiler
            isShrinkResources = true //Shrinking
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeCompiler {
        enableStrongSkippingMode = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation(libs.pushclient)

    implementation(platform(libs.tracer.platform))
    implementation(libs.tracer.crash.report)
    implementation(libs.tracer.crash.report.native)
    implementation(libs.tracer.heap.dumps)
    implementation(libs.tracer.disk.usage)
    implementation(libs.tracer.profiler.sampling)
    implementation(libs.tracer.profiler.systrace)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    implementation(libs.lottie.compose)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.animation)

    implementation(libs.hilt.android)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.ui.tooling.preview.android)
    "baselineProfile"(project(":baselineprofile"))
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose.v120)

    implementation(libs.junit.jupiter)
    implementation(libs.androidx.runtime.livedata)

    testImplementation(libs.core.ktx)
    testImplementation(libs.truth)
    androidTestImplementation(libs.androidx.work.testing)
    testImplementation(libs.androidx.core.v127)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.gson)
    implementation(libs.jsoup)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.ui.tooling)
    implementation(libs.androidx.material3)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}