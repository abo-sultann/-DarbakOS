# P1 — build and initial test

Separate TEST app: `com.abosultan.darbakos.test`, version `0.1.0-p1`.
No HOME role, boot receiver, permissions, runtime dependencies or hardware access.
Do not present it as Stable or install it on the T3 as the first experiment.

## Reproduce from a clean checkout

Requirements: JDK17, Android SDK command-line tools/platform-tools, Python3, and access
to Google SDK/Maven, Maven Central and the Gradle distribution service.
The official checked-in wrapper pins Gradle 8.9; AGP is 8.7.3.

```sh
git clone https://github.com/abo-sultann/DarbakOS.git
cd DarbakOS
sdkmanager 'platforms;android-35' 'build-tools;34.0.0' 'platform-tools'
# Set ANDROID_HOME to your SDK (or sdk.dir in local.properties).
python3 scripts/check_baseline.py
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug --no-daemon --console=plain
```

Windows: use `gradlew.bat` and `python` if that is the installed Python command.
Output: `app/build/outputs/apk/debug/app-debug.apk`.
Compile SDK35; minSdk/targetSdk25 for this sideload test; Java8 source/bytecode.
There are no native `.so` files or x86-only runtime dependencies excluding ARMv7.
AndroidX dependencies are confined to the separate instrumentation test APK.

## API25 emulator

Use one disposable API25/default/x86 emulator: 1024x600 pixels, 160dpi, 1024MB RAM,
128MB heap, software GPU. GitHub workflow `.github/workflows/p1-android.yml` provisions it.
For an existing matching emulator after the build:

```sh
python3 scripts/emulator_smoke.py
```

The script refuses physical devices, installs both APKs, sets emulator display/animation
configuration, and runs four Android instrumentation tests:

1. API25 launch, landscape, RTL, test label and unavailable speed.
2. Map/Media/Vehicle/Apps/Settings selection and Back to Home.
3. Activity recreation retains the section; Return Home works.
4. Measured 1024x600 view/text bounds, physical left speed and RTL navigation.

It then taps UIAutomator-derived button coordinates, captures Home and five section
screenshots, verifies a cold process restart opens Home, and saves crash/logcat, PSS,
CPU and graphics snapshots. APK size/SHA256, commit and evidence go to `test-evidence/`.
Failures preserve logs. CPU/gfx snapshots are observations, not benchmarks.
Visually inspect the screenshots before P1 acceptance.

Code pushes trigger **P1 Android baseline**; documentation-only checkpoints do not rebuild.
The artifact `DarbakOS-P1-TEST-<commit>` holds the APKs, lint report and evidence for 14 days.
Durable results/run links belong in TEST_RESULTS; selected screenshots can be kept in docs.

## Scope limits

No map engine, media playback, sensors, app enumeration, GPS, Trip, network, autostart,
root, firmware, MCU or OEM replacement. No repeated timers, large images or background work.
The explicit test label and unavailable speed remain until genuine sources exist.
System fonts and small vector/shape resources only.

API25/x86 results do not prove T3/ARMv7 memory, GPU, touch or wake behavior.
Test Station and exact-device acceptance remain separate master-plan gates.
