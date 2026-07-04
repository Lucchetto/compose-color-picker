plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.7.0"
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting  {
            dependencies {
                implementation(project(":compose-color-picker"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}
