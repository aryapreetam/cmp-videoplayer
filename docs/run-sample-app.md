# Running the Sample App

This guide describes how to run the `cmp-videoplayer` sample application, either using pre-built release executables or running from source.

---

## 📦 How to Run Pre-built Executables

### 🤖 Android
**Option 1: Drag and Drop**
- Download the APK file
- Open an Android emulator or connect a physical device
- Drag the APK onto the emulator window

**Option 2: ADB Install**
```bash
adb install sample-app-android-unsigned.apk
```

### 🌐 Web (Wasm)
- Download and unzip `sample-app-wasm.zip`
- Open `index.html` in a web browser

**Note:** You can also try the live demo without downloading: [Try Live Demo](https://aryapreetam.github.io/cmp-videoplayer/demo/)

### 🍏 iOS Simulator
- Download `sample-app-ios-simulator.zip` from the [latest release](https://github.com/aryapreetam/cmp-videoplayer/releases)
- Unzip to get `sample-app-ios-simulator.app`
- Open your iOS Simulator in Xcode
- Drag the `.app` onto the Simulator window OR run:
  ```sh
  xcrun simctl install booted /path/to/sample-app-ios-simulator.app
  ```
- The sample app will now appear and can be launched directly from the Simulator!

### 🍎 macOS
- Download the DMG for your Mac architecture (Intel or Apple Silicon)
- Open (mount) the DMG and drag the app to your Applications folder (or Desktop)
- **When you try to open the app for the first time, macOS Gatekeeper will block it since it is an open-source build signed ad-hoc (not using a paid Apple Developer account). Follow these 6 sequential steps to allow running the app:**

  <table width="100%">
    <tr>
      <td align="center" valign="top"><strong>1. Block Alert Dialog</strong><br/><br/><img src="../readme_images/mac_app_run1.png" alt="mac_app_run1" width="220"/></td>
      <td align="center" valign="top"><strong>2. Open System Settings</strong><br/><br/><img src="../readme_images/mac_app_run2.png" alt="mac_app_run2" width="220"/></td>
      <td align="center" valign="top"><strong>3. Privacy & Security Section</strong><br/><br/><img src="../readme_images/mac_app_run3.png" alt="mac_app_run3" width="220"/></td>
    </tr>
    <tr>
      <td align="center" valign="top"><strong>4. Click "Open Anyway"</strong><br/><br/><img src="../readme_images/mac_app_run4.png" alt="mac_app_run4" width="220"/></td>
      <td align="center" valign="top"><strong>5. Authenticate Security Dialog</strong><br/><br/><img src="../readme_images/mac_app_run5.png" alt="mac_app_run5" width="220"/></td>
      <td align="center" valign="top"><strong>6. Click "Open" to Launch</strong><br/><br/><img src="../readme_images/mac_app_run6.png" alt="mac_app_run6" width="220"/></td>
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

### 🪟 Windows
- Download and run the MSI installer
- Follow the installation wizard
- Launch the app from the Start menu

### 🐧 Linux
- Download the `.deb` file
- Double-click the file to open it in your software manager and click "Install", OR run:
  ```bash
  sudo dpkg -i sample-app-linux.deb
  ```
- Launch the application from your desktop launcher or run `sample` in the terminal!

---

## 🛠️ Run Sample App from Source

To run the sample app locally on your machine:

* **Desktop JVM:** `./gradlew :sample:composeApp:run`
* **Android:** Open project in Android Studio and run the sample app
* **iOS:** Open `sample/iosApp/iosApp.xcodeproj` in Xcode and run the sample app
* **Wasm:** `./gradlew :sample:composeApp:wasmJsBrowserRun`
