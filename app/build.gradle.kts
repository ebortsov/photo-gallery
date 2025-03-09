plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.gradle.secrets)
//    alias(libs.plugins.devtools.ksp)
}

secrets {
    propertiesFileName = "secrets.properties"

    defaultPropertiesFileName = "local.default.properties"
}

android {
    namespace = "com.github.ebortsov.photogallery"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.github.ebortsov.photogallery"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // For lifecycle coroutines
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Various UI components
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment.ktx)

    // For asynchronous http queries
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // For converting response bodies from OkHttp
    implementation(libs.retrofit2.converter.scalars)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)
//    ksp(libs.moshi.kotlin.codegen)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Coil (image loading and caching)
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.3")

    // Worker
    implementation("androidx.work:work-runtime-ktx:2.10.0")

}