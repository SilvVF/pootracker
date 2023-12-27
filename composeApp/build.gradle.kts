import org.jetbrains.compose.ExperimentalComposeLibrary


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    kotlin("plugin.serialization").version(libs.versions.kotlin.get())
    id("app.cash.sqldelight") version "2.0.1"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.sqldelight.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
        commonMain.dependencies {
            implementation(libs.molecule.runtime)

            implementation(libs.koin.core)
            implementation(libs.koin.test)

            implementation(libs.voyager.koin)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.primitive.adapters)

            implementation(libs.kotlinx.datetime)

            implementation(project.dependencies.platform("io.github.jan-tennert.supabase:bom:2.0.2"))
            implementation("io.github.jan-tennert.supabase:postgrest-kt")
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("io.silv")
        }
    }
}

android {
    namespace = "io.silv.pootracker"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "io.silv.pootracker"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

