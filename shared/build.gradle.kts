import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                JavaVersion.VERSION_21.toString()
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotinx.datetime)
            api(libs.kmp.viewmodel)
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.native.driver)
        }
    }
}

android {
    namespace = "com.leondeklerk.wheremybike"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.leondeklerk.wheremybike.shared.db")
        }
    }
}

// https://github.com/cashapp/sqldelight/issues/5310
project.afterEvaluate {
    val generatedSourcesDirectory = File(
        project.layout.buildDirectory.asFile.get(),
        "generated/sqldelight/code/Database/commonMain"
    )
    val kotlinProject = project.extensions.getByName("kotlin") as KotlinProjectExtension
    kotlinProject.sourceSets.getByName("commonMain").kotlin.srcDir(generatedSourcesDirectory)
}
