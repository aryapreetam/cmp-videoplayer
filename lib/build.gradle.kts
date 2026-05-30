@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.multiplatform)
  alias(libs.plugins.android.kotlin.multiplatform.library)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.dokka)
}

kotlin {
  jvmToolchain(17)

  androidLibrary {
    namespace = "io.github.aryapreetam.cmpvideoplayer"
    compileSdk = 35
    minSdk = 23
    
    // Enabling Android Resource Processing under KMP to support shared assets (e.g. composeResources) safely.
    androidResources {
      enable = true
    }
  }
  jvm()

  wasmJs { browser() }
  iosX64()
  iosArm64()
  iosSimulatorArm64()

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.runtime)
      implementation(libs.compose.ui.multiplatform)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.materialIconsExtended)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
      implementation(libs.compose.ui.test)
    }

    androidMain.dependencies {
      implementation(libs.media3.exoplayer)
      implementation(libs.media3.ui)
      implementation(libs.lifecycle.runtime.compose)
    }

    jvmMain.dependencies {
      implementation(libs.mediamp.all)
    }

  }

  //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
  targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    compilations["main"].compileTaskProvider.configure {
      compilerOptions {
        freeCompilerArgs.add("-Xexport-kdoc")
      }
    }
  }

}

// NOTE: Host-specific dependency leakage guardrail:

// DO NOT import host-specific binary dependencies (e.g. `compose.desktop.currentOs`) under library targets.
// Any desktop UI implementation should target standard platform-agnostic `jvm()` targets.
// Platform-specific runtime locators must be restricted solely to the executable sample application (:sample).

// Compose UI tests on JVM/desktop may require additional native runtime setup.
// We keep such tests in the source tree (as documentation/examples), but exclude them
// from default unit test runs.
tasks.withType<Test>().configureEach {
  exclude("**/*UITest*")
}

dependencies {
  dokkaPlugin(libs.android.documentation.plugin)
}

//Publishing your Kotlin Multiplatform library to Maven Central
//https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html
mavenPublishing {
  publishToMavenCentral()
  coordinates(
      project.group.toString(),
      findProperty("libArtifactId")?.toString() ?: "cmp-videoplayer",
      project.version.toString()
  )

  pom {
    name = "Video player for CMP"
    description = "Video player for Compose Multiplatform(Android, iOS, Web(wasm), Desktop)"
    url = "https://aryapreetam.github.io/cmp-videoplayer" //todo

    licenses {
      license {
        name = "MIT"
        url = "https://opensource.org/licenses/MIT"
      }
    }

    developers {
      developer {
        id = "aryapreetam" //todo
        name = "Preetam Bhosle" //todo
      }
    }

    scm {
      url = "https://github.com/aryapreetam/cmp-videoplayer" //todo
    }
  }
  // Sign publications if either local keyId or CI signingInMemoryKey is available
  if (project.hasProperty("signing.keyId") || project.hasProperty("signingInMemoryKey")) {
    signAllPublications()
  }
}

