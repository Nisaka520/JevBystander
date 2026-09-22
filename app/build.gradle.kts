plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.nisaka520.jevbystander"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.nisaka520.jevbystander"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false          // 无第三方依赖，不需要混淆；要瘦身可自行开启
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources.excludes += setOf("META-INF/*.kotlin_module")
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    // 运行期零第三方依赖：JSON 用手写的 Json.kt，网络用 HttpURLConnection，UI 用原生 View
    testImplementation("junit:junit:4.13.2")
}

// 单测输出中文，Windows 上默认编码会乱，锁成 UTF-8；顺便把 println 显示出来
tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    systemProperty("file.encoding", "UTF-8")
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
