import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val releaseStoreFile: String = System.getenv("RELEASE_STORE_FILE") ?: localProperties.getProperty("RELEASE_STORE_FILE") ?: ""
val releaseStorePassword: String = System.getenv("RELEASE_STORE_PASSWORD") ?: localProperties.getProperty("RELEASE_STORE_PASSWORD") ?: ""
val releaseKeyAlias: String = System.getenv("RELEASE_KEY_ALIAS") ?: localProperties.getProperty("RELEASE_KEY_ALIAS") ?: ""
val releaseKeyPassword: String = System.getenv("RELEASE_KEY_PASSWORD") ?: localProperties.getProperty("RELEASE_KEY_PASSWORD") ?: ""

android {
    namespace = "com.chittortech.snp"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.chittortech.snp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            if (releaseStoreFile.isNotEmpty()) {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true       // R8 obfuscation — hides class names, credential strings, API paths
            isShrinkResources = true     // Remove unused resources from APK
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        aidl = false
        buildConfig = true
        shaders = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.auth)
    implementation(libs.ktor.okhttp)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.okhttp)

    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}


