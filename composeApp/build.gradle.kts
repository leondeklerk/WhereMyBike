plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.leondeklerk.wheremybike"
        compileSdk = 36
        minSdk = 24

        androidResources.enable = true

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "composeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // JetBrains Navigation & Lifecycle
            implementation(libs.navigation.compose)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)

            // KotlinX
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            // Database
            implementation(libs.sqldelight.coroutines.extensions)

            // Dependency Injection
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            // SQLDelight Android Driver
            implementation(libs.sqldelight.android.driver)

            // Android specific
            implementation(libs.androidx.activity.compose)
            implementation(libs.maps.compose)
            implementation(libs.play.services.maps)
            implementation(libs.play.services.location)
            implementation(libs.accompanist.permissions)

            // Koin Android
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            // SQLDelight iOS Driver
            implementation(libs.sqldelight.native.driver)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.leondeklerk.wheremybike.resources"
    generateResClass = always
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.leondeklerk.wheremybike.db")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/migrations"))
            verifyMigrations.set(true)
        }
    }
}
