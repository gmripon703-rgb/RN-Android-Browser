# Browser RN

An ultra-lightweight, mobile-responsive, zero-telemetry Android web browser designed for smartphones, tablets, and **Android TV / Google TV**. Built with Kotlin and Jetpack Compose.

---

## Key Features

1. **Zero-Login Free Cryptographic Key (100% Wire-Speed)**:
   - **No account, sign-in, email, or password required** to connect.
   - Automatically provisions an open-source Curve25519 cryptographic token directly on the device.
   - **0% speed loss** via direct Anycast TLS 1.3 / UDP routing (Cloudflare 1.1.1.1, Quad9 Swiss, AdGuard).

2. **ISP PPPoE & Hacker Log Protection**:
   - Zero local browsing logs: browsing history and cache are isolated to volatile memory.
   - Conceals DNS resolutions and suppresses `X-Forwarded-For`, `Client-IP`, and PPPoE router sniffing.
   - 1-Tap **Stealth Erase**: instagenerates a clean slate by purging tabs, WebStorage, DOM storage, cookies, and cache.

3. **Open-Source Flash Player & Media Engine**:
   - Integrates **Ruffle** (the WebAssembly/HTML5 Flash Player emulator) to seamlessly run legacy `.swf` files, Flash animations, and games on modern Android without security risks.
   - Full HTML5 video/audio playback with hardware acceleration (`hardwareAccelerated="true"`), fullscreen video player, and unthrottled media streaming.

4. **Mobile Responsive & Desktop View**:
   - Dynamic viewport scaling: supports all Android screen sizes and densities.
   - 1-tap toggle between **Mobile Responsive Layout** and **Full Desktop Site**.
   - Adjustable text and page zoom (50% to 200%).

5. **Android TV & Google TV Remote Support**:
   - Touchscreen optional (`android.hardware.touchscreen=false`).
   - Dedicated Android TV Leanback launcher category and 16:9 banner.
   - Built-in on-screen TV navigation dock and hardware D-Pad / Page Up / Page Down support for remote controllers.

---

## Developing & Building from GitHub

### Prerequisites
- **Android Studio** (Hedgehog, Iguana, Jellyfish, Koala, Ladybug, or newer)
- **JDK 17** or **JDK 21** (bundled with modern Android Studio)
- Android SDK 34/35/36 installed via SDK Manager

---

### Instructions for Windows 11

1. **Clone the repository**:
   ```powershell
   git clone https://github.com/your-username/browser-rn.git
   cd browser-rn
   ```

2. **Open in Android Studio**:
   - Launch **Android Studio**.
   - Click **Open** and select the cloned `browser-rn` folder.
   - Wait for Android Studio to complete Gradle sync.

3. **Build the Debug APK via Terminal**:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
   The APK will be generated at:
   `app\build\outputs\apk\debug\app-debug.apk`

4. **Run Unit & Robolectric Tests**:
   ```powershell
   .\gradlew.bat testDebugUnitTest
   ```

---

### Instructions for Linux (Ubuntu, Debian, Fedora, Arch)

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/browser-rn.git
   cd browser-rn
   ```

2. **Make the Gradle wrapper script executable**:
   ```bash
   chmod +x gradlew
   ```

3. **Open in Android Studio**:
   - Launch Android Studio (`studio.sh` or through your application menu).
   - Select **Open** and browse to the `browser-rn` project directory.
   - Allow Gradle to sync dependencies.

4. **Build the Debug APK via Terminal**:
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be generated at:
   `app/build/outputs/apk/debug/app-debug.apk`

5. **Run Unit & Robolectric Tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## Architecture & Tech Stack

- **UI Framework**: Modern Jetpack Compose with Material Design 3 (M3)
- **Engine**: Android WebView with WebGL, WebAssembly, and HTML5 Canvas acceleration
- **Flash Emulation**: Open-source Ruffle WebAssembly integration
- **Privacy & DNS**: Encrypted DNS over HTTPS (DoH) via Cloudflare 1.1.1.1, Quad9, AdGuard
- **VPN Tunnel**: Android `VpnService` with auto-generated open-source keys
- **Local Persistence**: Room Database (for user bookmarks only, zero activity logging)
- **Platform Compatibility**: Android 6.0+ (API 23+) through Android 15 & 16, Mobile, Tablets, Foldables, and Google/Android TV

---

## Developer & Contact

**Developed by GM Ripon**

- **WhatsApp**: [+8801911527072](https://wa.me/8801911527072)
- **Telegram**: [+8801911527072](https://t.me/+8801911527072)
- **imo**: [+8801911527072](https://imo.im) / Direct contact
- **Email**: [gmripon703@gmail.com](mailto:gmripon703@gmail.com)
- **Direct Phone**: `+8801911527072`

