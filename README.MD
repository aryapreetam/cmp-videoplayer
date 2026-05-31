<h1 align="center">CMP Video Player - Compose Multiplatform Video Player Library</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-v1.10.1-4285F4?logo=jetpackcompose" alt="Compose Multiplatform Version">
  <img src="https://img.shields.io/badge/Kotlin-v2.3.21-blueviolet?logo=kotlin" alt="Kotlin Version">
</p>

<p align="center">
  <a href="https://github.com/aryapreetam/cmp-videoplayer/actions/workflows/release.yml">
    <img src="https://github.com/aryapreetam/cmp-videoplayer/actions/workflows/release.yml/badge.svg" alt="Release workflow status">
  </a>
  <a href="https://mvnrepository.com/artifact/io.github.aryapreetam/cmp-videoplayer">
    <img src="https://img.shields.io/maven-central/v/io.github.aryapreetam/cmp-videoplayer?label=Maven%20Central&color=blue" alt="Maven Central Version">
  </a>
  <a href="https://github.com/aryapreetam/cmp-videoplayer/blob/main/LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="MIT License">
  </a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white" alt="iOS">
  <img src="https://img.shields.io/badge/Desktop/JVM-0078D6?logo=desktop&logoColor=white" alt="Desktop">
  <img src="https://img.shields.io/badge/Web(wasm)-4285F4?logo=google-chrome&logoColor=white" alt="Web">
</p>

`cmp-videoplayer` is an OSS **Compose Multiplatform** video player component that focuses on one responsibility: **playing video files** across targets.

Supported targets:
- Android (Media3 ExoPlayer)
- iOS (AVPlayer)
- Desktop/JVM (mediamp + VLC)
- Web (wasmJs; HTML5 `<video>`)

Non-goals:
- Embedded providers (YouTube/Vimeo/iframes)
- Poster/image loading (use the `placeholder` slot)

Wasm Demo: 
[![Try Live Demo](https://img.shields.io/badge/Try%20Live%20Demo-Web%20wasmJs-blueviolet?style=for-the-badge)](https://aryapreetam.github.io/cmp-videoplayer/demo/)

API Docs:
[![Library API Docs](https://img.shields.io/badge/API%20Reference-Library%20Docs-green?style=for-the-badge)](https://aryapreetam.github.io/cmp-videoplayer/api/)

---

## Quick start

### Dependency

Add the dependency to your `commonMain`:

```kotlin
kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation("io.github.aryapreetam:cmp-videoplayer:<version>")
    }
  }
}
```

### Basic usage

```kotlin
@Composable
fun MyScreen() {
  VideoPlayer(
    source = VideoSource.Url("https://example.com/video.mp4"),
    config = VideoPlayerConfig(
      autoplay = false,
      showControls = true,
    )
  )
}
```

### Sources
- `VideoSource.Url(url)` supports `http(s)://`, `file://`, and on Android also `content://` and `android.resource://`.
- `VideoSource.Path(path)` is a raw filesystem path (Android/iOS/JVM). On wasm, use `Url`.

---

---

## wasm: playing `Res.raw.*` videos

On `wasmJs`, the HTML `<video>` element needs a URL. If your video is only available via Compose resources (`Res.raw.*`), use `rememberBlobVideoUrl`:

```kotlin
@Composable
fun ResourceVideo(bytes: ByteArray) {
  // `mimeType` is explicit by design.
  val url = rememberBlobVideoUrl(bytes, mimeType = "video/mp4")
  VideoPlayer(VideoSource.Url(url))
}
```

Notes:
- `blob:` URLs are revoked on disposal.
- For large videos, serving them as a static file URL is more memory-efficient.

---

## Testing guidance (important)

The library uses platform interop for real playback. For UI tests, you can force a pure-Compose render path:

```kotlin
CompositionLocalProvider(
  LocalVideoPlayerConfig provides VideoPlayerConfig(renderMode = RenderMode.PlaceholderOnly)
) {
  VideoPlayer(
    source = VideoSource.Url("https://example.com/video.mp4"),
    placeholder = { /* render your test-friendly placeholder UI */ }
  )
}
```

The root node uses the test tag `cmp-videoplayer:VideoPlayer`.

If using snapshots from Maven Local, ensure:

```kotlin
repositories {
  mavenLocal()
  mavenCentral()
}
```

---

## 📦 Download Sample App Executables

Download and try the sample app on your platform without building from source:

| Platform | Download Link |
|----------|--------------|
| 🍏 **macOS (Intel)** | [![Download](https://img.shields.io/badge/Download--Mac--x64--DMG-blue?style=for-the-badge)](https://github.com/aryapreetam/cmp-videoplayer/releases/latest/download/sample-app-desktop-x64.dmg) |
| 🍎 **macOS (Apple Silicon)** | [![Download](https://img.shields.io/badge/Download--Mac--arm64--DMG-blue?style=for-the-badge)](https://github.com/aryapreetam/cmp-videoplayer/releases/latest/download/sample-app-desktop-arm64.dmg) |
| 🪟 **Windows** | [![Download](https://img.shields.io/badge/Download--Windows--MSI-0078D4?style=for-the-badge&logo=windows)](https://github.com/aryapreetam/cmp-videoplayer/releases/latest/download/sample-app-windows.msi) |
| 🐧 **Linux** | [![Download](https://img.shields.io/badge/Download--Linux--DEB-E95420?style=for-the-badge&logo=ubuntu)](https://github.com/aryapreetam/cmp-videoplayer/releases/latest/download/sample-app-linux.deb) |
| 🤖 **Android** | [![Download](https://img.shields.io/badge/Download--Android--APK-green?style=for-the-badge&logo=android)](https://github.com/aryapreetam/cmp-videoplayer/releases/latest/download/sample-app-android-unsigned.apk) |
| 🌐 **Web (Wasm)** | [![Download](https://img.shields.io/badge/Download--Web--Wasm--zip-purple?style=for-the-badge)](https://github.com/aryapreetam/cmp-videoplayer/releases/latest/download/sample-app-wasm.zip) |
|  **iOS Simulator** | [![Download](https://img.shields.io/badge/Download--iOS--Simulator--zip-orange?style=for-the-badge&logo=apple)](https://github.com/aryapreetam/cmp-videoplayer/releases/latest/download/sample-app-ios-simulator.zip) |

### How to Run the Sample App

#### 🤖 Android
**Option 1: Drag and Drop**
- Download the APK file
- Open an Android emulator or connect a physical device
- Drag the APK onto the emulator window

**Option 2: ADB Install**
```bash
adb install sample-app-android-unsigned.apk
```

#### 🌐 Web (Wasm)
- Download and unzip `sample-app-wasm.zip`
- Open `index.html` in a web browser

**Note:** You can also try the live demo without downloading: [Try Live Demo](https://aryapreetam.github.io/cmp-videoplayer/demo/)

#### 🍏 iOS Simulator
- Download `sample-app-ios-simulator.zip` from the [latest release](https://github.com/aryapreetam/cmp-videoplayer/releases)
- Unzip to get `sample-app-ios-simulator.app`
- Open your iOS Simulator in Xcode
- Drag the `.app` onto the Simulator window OR run:
  ```sh
  xcrun simctl install booted /path/to/sample-app-ios-simulator.app
  ```
- The sample app will now appear and can be launched directly from the Simulator!

#### 🍎 macOS
- Download the DMG for your Mac architecture (Intel or Apple Silicon)
- Open (mount) the DMG and drag the app to your Applications folder (or Desktop)
- **When you try to open the app for the first time, macOS Gatekeeper will block it since it is an open-source build signed ad-hoc (not using a paid Apple Developer account). Follow these 6 sequential steps to allow running the app:**

  <table width="100%">
    <tr>
      <td align="center" valign="top"><strong>1. Block Alert Dialog</strong><br/><br/><img src="readme_images/mac_app_run1.png" alt="mac_app_run1" width="220"/></td>
      <td align="center" valign="top"><strong>2. Open System Settings</strong><br/><br/><img src="readme_images/mac_app_run2.png" alt="mac_app_run2" width="220"/></td>
      <td align="center" valign="top"><strong>3. Privacy & Security Section</strong><br/><br/><img src="readme_images/mac_app_run3.png" alt="mac_app_run3" width="220"/></td>
    </tr>
    <tr>
      <td align="center" valign="top"><strong>4. Click "Open Anyway"</strong><br/><br/><img src="readme_images/mac_app_run4.png" alt="mac_app_run4" width="220"/></td>
      <td align="center" valign="top"><strong>5. Authenticate Security Dialog</strong><br/><br/><img src="readme_images/mac_app_run5.png" alt="mac_app_run5" width="220"/></td>
      <td align="center" valign="top"><strong>6. Click "Open" to Launch</strong><br/><br/><img src="readme_images/mac_app_run6.png" alt="mac_app_run6" width="220"/></td>
    </tr>
  </table>

- To allow running the app:
     1. Double-click the app. If blocked, open macOS **System Settings** (or System Preferences).
     2. Navigate to **Privacy & Security** and scroll down to the **Security** section.
     3. You will see a notice stating: *"sample was blocked from use because it is not from an identified developer"*.
     4. Click **"Open Anyway"** and authenticate with your user password or Touch ID.
     5. Re-open the app and click **"Open"** on the confirmation dialog. The app will now launch safely!

> This is a normal security step for all open-source and CI-generated Mac executables. Apps are signed ad-hoc for internal/dev use, not with a public Apple developer ID.

**Tip:** Unsure about your Mac's type? Click the Apple logo → "About This Mac". If it says Intel, download x64; if it says M1, M2, or M3, download arm64.

#### 🪟 Windows
- Download and run the MSI installer
- Follow the installation wizard
- Launch the app from the Start menu

#### 🐧 Linux
- Download the `.deb` file
- Double-click the file to open it in your software manager and click "Install", OR run:
  ```bash
  sudo dpkg -i sample-app-linux.deb
  ```
- Launch the application from your desktop launcher or run `sample` in the terminal!

---

## 🚀 What This Library Provides

This library automatically deploys three key resources:

| Resource | Description | Preview |
|----------|-------------|---------|
| 📚 **Library Homepage** | Your README becomes a beautiful landing page with Docsify | <img src="readme_images/site-for-the-lib.png" alt="Library homepage" width="250" /> |
| 📖 **API Documentation** | Automatically generated Dokka docs with improved UI (2.2.0) | <img src="readme_images/ste-for-api-doc.png" alt="API docs" width="250" /> |
| 🌐 **Live Wasm Demo** | Interactive browser demo of your sample app | <img src="readme_images/wasm-demo.png" alt="Wasm demo" width="250" /> |

All three are automatically deployed to GitHub Pages:
- **Homepage:** `https://<username>.github.io/<library-name>/`
- **API Docs:** `https://<username>.github.io/<library-name>/api/`
- **Demo:** `https://<username>.github.io/<library-name>/demo/`

---

## 🛠️ Run Sample App from Source

You can also run Gradle tasks in the terminal:

* `./gradlew run` to run application
* `./gradlew package` to store native distribution into `build/compose/binaries`

### Run Sample App by Platform

- **Desktop JVM:** `./gradlew :sample:composeApp:run`
- **Android:** Open project in Android Studio and run the sample app
- **iOS:** Open `sample/iosApp/iosApp.xcodeproj` in Xcode and run the sample app
- **Wasm:** `./gradlew :sample:composeApp:wasmJsBrowserRun`

---

## CI/CD overview

This repo ships with an opinionated CI/CD that builds, tests, packages, releases, and deploys docs & demo automatically on tags.

```mermaid
flowchart TD
    A["Lint"] --> B["Build & Test - Library + Sample (all targets)"]
    B --> C["Android UI tests (emulator) & Maestro E2E tests(iOS & Android)"]
    C --> D["Build artifacts - APK • DMG (Intel/Apple Silicon) • MSI • DEB • iOS Simulator zip • wasm bundle"]
    D --> E["Create GitHub Release and upload artifacts"]
    E --> F["Publish to Maven Central"]
    E --> G["Deploy GitHub Pages README site (Docsify) + wasm demo + API docs"]
```

Key points:
- Triggered on version tags (`v*`) and manual runs.
- Lint first, then build/test library targets followed by sample targets.
- Android UI tests on emulator before packaging.
- GitHub Release includes platform artifacts for quick validation.
- GitHub Pages hosts the README homepage (Docsify), wasm demo, and Dokka API docs.

---

### Publish to MavenLocal

1) Run `./gradlew :cmp-videoplayer:publishToMavenLocal`
2) Open `~/.m2/repository/cmp-videoplayer/`

### Publish to MavenCentral

Follow https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html & complete all necessary steps.

The above article focuses on publishing to MavenCentral.
But if you want to verify publishing from your local machine, you can follow below steps:

1) Add these lines to `~/.gradle/gradle.properties`(not to be confused with `gradle.properties` in project root):
   ```
   signing.keyId=XXXXXXXX
   signing.password=[key password]
   signing.secretKeyRingFile=../XXXXXXXX.gpg
   mavenCentralUsername=[generated username]
   mavenCentralPassword=[generated password]
   ```
   For `GPG_KEY_CONTENTS` secret(signing.secretKeyRingFile above), can get plain text version of gpg key using below commands.
   ```
   # This will print the private GPG key in plain text.
   gpg --export-secret-keys --armor <key id>
   
   # this will copy it for pasting in github actions secrets.
   gpg --export-secret-keys --armor <key id> | pbcopy
   ```
2) Run `./gradlew :cmp-videoplayer:publishAndReleaseToMavenCentral --no-configuration-cache`
   
Now check the deployments here https://central.sonatype.com/publishing/deployments 


## License

MIT License © 2025 aryapreetam and contributors. See [LICENSE](./LICENSE) for details.

---

## 🙏 Acknowledgments

This template was built with inspiration and learnings from the excellent work of the Kotlin multiplatform community:

- **Project Setup:** Initial project structure was created using [Kotlin Multiplatform Web Wizard](https://terrakok.github.io/kmp-web-wizard/) by [terrakok](https://github.com/terrakok)

- **Inspirations:**
  - [Reorderable](https://github.com/Calvin-LL/Reorderable) by Calvin-LL - For a library that seamlessly works across all platforms, and most importantly, providing demo app APK in releases to test before integrating
  - [compose-multiplatform-library-template](https://github.com/KevinnZou/compose-multiplatform-library-template) by KevinnZou 
  - [compose-multiplatform-library-template](https://github.com/meticha/compose-multiplatform-library-template) by meticha 

Special thanks to:
- **GitHub** for hosting this project and providing free compute resources for CI/CD through GitHub Actions
- **JetBrains** for building the incredible Kotlin Multiplatform and Compose Multiplatform frameworks that enable us to build truly cross-platform applications with shared code
- The entire **Kotlin Multiplatform and Compose Multiplatform communities** for making cross-platform development better every day!

---

## 📋 TBD (To Be Done) - Future Enhancements

This section lists planned improvements and features for the template. Contributions are welcome!

### 🔧 Code Quality & Tooling

- [ ] **Add Detekt** - Static code analysis for Kotlin
  - Configure rules for library code
  - Integrate into CI pipeline
  
- [ ] **Add ktlint** - Kotlin code formatter
  - Consistent code style across project
  - Auto-formatting in CI
  
- [ ] **Add Kover** - Code coverage reporting
  - Track test coverage across platforms
  - Publish coverage reports to Codecov
  
- [ ] **Add Dependabot** - Automated dependency updates
  - Weekly checks for Gradle dependencies
  - Weekly checks for GitHub Actions versions

### 🧪 Testing Enhancements

- [ ] **Screenshot Testing** - Visual regression testing
  - Add Paparazzi for Android
  - iOS screenshot tests using XCTest
  
- [ ] **Performance Benchmarking** - Track performance metrics
  - Integrate androidx.benchmark for Android
  - Baseline profiles for library code

### 📚 Documentation & Examples

- [ ] **Multi-Module Support Guide**
  - Document how to structure multi-module libraries
  - Example with feature modules
  
- [ ] **Compose Resources Guide**
  - How to bundle images, strings, fonts
  - Localization examples
  - Resource packaging best practices
  
- [ ] **Video Tutorial**
  - 10-minute walkthrough of using the template
  - Publishing to Maven Central demo
  - Deploying demo and docs
  
- [ ] **Blog Post / Article**
  - Write comprehensive guide on Dev.to or Medium
  - Share on Reddit r/Kotlin and r/Kotlinmobile

### 🤖 Automation Improvements

- [ ] **Automated Changelog Generation**
  - Use conventional commits
  - Auto-generate release notes from commits/PRs
  - Integrate github-changelog-generator
  
- [ ] **Version Bump Automation**
  - Script to update version in cmp-videoplayer/build.gradle.kts
  - Semantic versioning support
  - Consider semantic-release integration
  
- [ ] **Automated Screenshots**
  - Generate sample app screenshots during CI
  - Attach to releases or README

### 🖥️ Platform Support

- [ ] **Linux Desktop Packaging**
  - Add DEB/RPM package generation
  - AppImage support
  
- [ ] **Additional Apple Platforms**
  - watchOS support
  - tvOS support
  - macCatalyst support
  
- [ ] **Desktop Linux Native Build**
  - Build on Linux runners for native Linux apps
  - Better Linux desktop integration

### 🌐 GitHub Pages Enhancements

- [ ] **Custom Domain Support**
  - Document setup for custom domains
  - DNS configuration guide
  
- [ ] **Improved Docsify Theme**
  - Custom theme matching library branding
  - Dark mode support
  - Better mobile responsive design
  
- [ ] **Search Functionality**
  - Add search to documentation site
  - Full-text search across API docs

### 🔐 Security & Best Practices

- [ ] **Vulnerability Scanning**
  - Integrate Snyk or similar
  - Automated security updates
  
- [ ] **SBOM Generation**
  - Generate Software Bill of Materials
  - Dependency transparency
  
- [ ] **Signed Releases**
  - Sign release artifacts with GPG
  - Provide checksums (SHA256)

### 📦 Distribution & Publishing

- [ ] **JitPack Support**
  - Alternative to Maven Central
  - Easier for snapshot builds
  
- [ ] **NPM Package for Wasm**
  - Publish wasm builds to NPM
  - Better integration with web projects
  
- [ ] **CocoaPods Support**
  - Publish iOS framework to CocoaPods
  - Easier iOS integration

### 🎨 Template Features

- [ ] **Template Initialization Script**
  - Interactive CLI to customize template
  - Automated find/replace for package names
  - Git history cleanup

---

### 💡 Have an Idea?

If you have suggestions for improvements or want to contribute:
1. Check if the feature is already listed above
2. Open an issue to discuss your idea
3. Submit a PR with your implementation

**Priority**: Items marked with 🔥 are high priority and would have the most impact.

---

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed contribution guidelines.
