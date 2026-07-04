import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.jetbrains.compose") version "1.7.0"
    id("com.android.library")
    id("com.vanniktech.maven.publish") version "0.29.0"
}

kotlin {
    android("android") {
        publishLibraryVariants("release")
    }
    jvm()
    js(IR) {
        browser()
    }
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                implementation(compose.material)
                implementation("com.github.ajalt.colormath:colormath:3.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.preview)
            }
        }
        val jvmTest by getting
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

android {
    namespace = "io.github.lucchetto.colorpicker"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

val dokkaOutputDir = buildDir.resolve("dokka")

tasks.dokkaHtml.configure {
    outputDirectory.set(dokkaOutputDir)
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

mavenPublishing {
    coordinates("io.github.lucchetto.colorpicker", "compose-color-picker", "0.7.1")

    pom {

        name.set("compose-color-picker")
        description.set("A multiplatform Compose component for picking a color (Android, JVM, JS, iOS)")
        url.set("https://github.com/Lucchetto/compose-color-picker")

        licenses {
            license {
                name.set("The MIT License (MIT)")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("Lucchetto")
                name.set("Lucchetto")
            }
        }
        organization {
            name.set("Lucchetto")
        }
        scm {
            connection.set("scm:git:git://github.com/Lucchetto/compose-color-picker.git")
            developerConnection.set("scm:git:ssh://git@github.com/Lucchetto/compose-color-picker.git")
            url.set("https://github.com/Lucchetto/compose-color-picker")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (hasProperty("signing.keyId")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("signing.password")    // configured in the ~/.gradle/gradle.properties file
        && hasProperty("signing.secretKeyRingFile")    // configured in the ~/.gradle/gradle.properties file
    ) {
        signAllPublications()
    }
}
