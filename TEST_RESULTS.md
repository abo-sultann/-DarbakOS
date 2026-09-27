# Test Results

No Darbak OS release is Stable. See the per-stage evidence below.

## 2026-09-27 — P1 implementation checkpoint
- Base commit: `53c4b92d36ef9e3d4cebb7d5a11fed2fa2cfbc7d`; implementation is the commit containing this entry.
- Work Linux x86_64, OpenJDK17.0.20; no Android SDK/adb/emulator/KVM available.
- PASS: `python3 scripts/check_baseline.py` (XML, API25, RTL/landscape, no permissions/background/native/runtime dependency drift).
- PASS: Python compilation for both scripts, `bash -n gradlew`, `git diff --check`.
- BLOCKED locally: `./gradlew --version` failed fetching Gradle8.9 (`java.net.SocketException: Network is unreachable`). SDK probe timed out at proxy; no local APK produced.
- NOT RUN yet: build/lint, API25 launch/navigation/recreation/1024x600 fit, runtime RAM/CPU and crash checks. GitHub Actions is prepared.
- No Test Station/T3 testing or hardware/system changes.
- Follow-up: finish P1 verification per `02_NEXT_TASK.md`; no P1 acceptance without measured evidence.

## P1 CI correction — same batch
- Implementation commit `6d784f318604c63354cdc5e5b367c2dcea0f60c0` reached GitHub main.
- [Run 36306645815](https://github.com/abo-sultann/DarbakOS/actions/runs/36306645815): source checks and official Gradle wrapper verification passed; SDK step failed with `sdkmanager: command not found` (exit127). Build/emulator steps were skipped; no APK/runtime pass.
- Correction: explicitly provision the SDK command-line tools with `android-actions/setup-android@v3` before sdkmanager; do not rely on preinstalled runner tools. Rerun required.

## Test stages
## P1 visual correction — same batch
- [Run 36306734372](https://github.com/abo-sultann/DarbakOS/actions/runs/36306734372), commit `695c614a4f8535ab296f25173f2af6fcaed446e8`: build/lint, all 4 instrumentation tests, UI navigation and cold restart passed. Six screenshots inspected; no clipped text, but the RTL speed-card margin landed on the outer left edge and removed the intended inter-card gap.
- Corrected that margin from end to start on the explicitly RTL card; strengthened the existing 1024x600 test to assert the 16dp inter-card gap. Revalidation pending.
- Preliminary measurements: APK 18,217 bytes; launch TotalTime 296ms; process PSS 8,713KB (about 8.51MiB). These are x86 emulator observations, not T3 performance claims.
- Crash buffer empty; no app ANR detected. CPU snapshot covered an earlier boot interval with no app process, so app CPU is NOT MEASURED (not 0%).
- Lint had no errors; warnings concern pinned test-dependency versions, deliberate landscape, simple nested weights/baseline alignment/overdraw and spare color tokens. No runtime dependencies/native libraries in the APK.

## Test stages
1. Laptop/emulator initial validation
2. Darbak Test Station (Acer/BlissOS) where useful
3. Real Allwinner T3 commissioning/validation
4. Stable acceptance

For every test record: date, commit, environment, API/ABI/resolution, scenario, result, RAM/CPU where available, crash/ANR/log notes, screenshots/report reference, and follow-up.
