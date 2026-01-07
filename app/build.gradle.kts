// File: app/build.gradle.kts

// 1. SEÇÃO DE PLUGINS
//    Os aliases aqui (ex: libs.plugins.android.application) são definidos no seu libs.versions.toml
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // Plugin KSP, essencial para o Room funcionar. O erro "Unresolved reference: ksp" é corrigido aqui.
    alias(libs.plugins.google.devtools.ksp)
}

// 2. SEÇÃO ANDROID
android {
    namespace = "com.example.ecotracker" // Troque para o seu nome de pacote real
    compileSdk = 34 // API Level 34 (Android 14) é o padrão para novas aplicações

    defaultConfig {
        applicationId = "com.example.ecotracker"
        minSdk = 24 // Android 7.0 (Nugat) é um bom ponto de partida
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true // Habilita o Jetpack Compose
    }
    composeOptions {
        // Usa a versão do compilador do Compose que é compatível com a versão do Kotlin
        // que definimos em libs.versions.toml (Kotlin 1.9.23)
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// 3. SEÇÃO DE DEPENDÊNCIAS
//    Aqui usamos os aliases definidos em libs.versions.toml
dependencies {

    // --- Dependências Padrão (Core, Lifecycle) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- Jetpack Compose ---
    // Importa o Bill of Materials (BOM) do Compose.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- Room (Banco de Dados) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Essencial para usar Coroutines e Flow com Room
    ksp(libs.androidx.room.compiler)        // O processador de anotações para o Room
    implementation(libs.paging.runtime) // lib para paginação dos dados retornados
    implementation(libs.paging.compose)

    // --- Acesso a API ---
    implementation(libs.androidx.retrofit.core)
    implementation(libs.androidx.retrofit.gson)
    // --- Testes ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}