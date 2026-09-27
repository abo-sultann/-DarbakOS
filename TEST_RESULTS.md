# Test Results

No Darbak OS release is Stable. See the per-stage evidence below.

## 2026-09-27 — P1 final acceptance at initial emulator stage

**PASS. Code:** `72fde4a84d57e52830cd509f2556ac867e2777e5`.
[Successful GitHub Actions run](https://github.com/abo-sultann/DarbakOS/actions/runs/36306964115).
This closes P1 only; it is not real-T3 or Stable acceptance.

| Check | Actual result |
|---|---|
| Build environment | Ubuntu24.04 GitHub runner; JDK17, Gradle8.9, AGP8.7.3; compileSdk35/build-tools34.0.0 |
| Runtime environment | API25/default/x86, 1024x600 pixels, 160dpi, 1024MB RAM/128MB heap, software GPU |
| Build | Debug app and instrumentation APKs produced; build/lint step passed |
| Lint | Zero errors; 11 warnings described below, no blanket lint suppression |
| Instrumentation | **4/4 passed**, 2.664s: launch/RTL/test state; all destinations+Back; recreation+Home; measured fit+16dp inter-card gap |
| UI smoke | UIAutomator-coordinate taps through Map/Media/Vehicle/Apps/Settings, screenshots and cold process restart to Home passed |
| Visual review | Six sections reviewed in preceding run; corrected Home rechecked in final run. Arabic text fits, speed left, nav RTL, final card gap visible |
| APK size | **18,221 bytes** (17.79KiB); no bundled native `.so` libraries |
| Launch | `am start -W`: Status ok; TotalTime **353ms**, WaitTime355ms (single launch, not benchmark) |
| Process memory | PSS **8,636KB**, about **8.43MiB**; one Activity/39 Views; snapshot after restart |
| CPU | **Not measured validly**: dumpsys window preceded the app process. Do not interpret as 0% |
| Crash/ANR | Empty crash buffer and no app ANR detected during this run |
| T3/ARMv7/Test Station | **Not tested**; all exact-unit safety/acceptance gates remain open |

APK SHA-256: `b392e732bf64140376aea61d08c61bf77927cc7ecd349e6f7062a69097e13722`.
Downloaded artifact was checked against this hash. Artifact id: `10927367313`, expires 2026-10-11.
Durable selected raw observations and Home/Settings screenshots: [docs/test-evidence/p1-20260927](docs/test-evidence/p1-20260927).
The Actions artifact contains both APKs, all six screenshots, UI XML, full logs and lint report.

Lint warnings are four newer test-library suggestions, deliberate fixed landscape, two baseline-alignment suggestions,
one nested-weight warning, background overdraw and two spare color tokens. Only the Play Store expired-target
check is disabled explicitly because this P1 build is an API25 sideload test. No functional/API errors remain.
No CPU/GPU/long-run/battery claim is inferred from these short tests.

**Follow-up:** one bounded P2 Home test-state increment in `02_NEXT_TASK.md`. No P2 work started in this batch.

## 2026-09-27 — P1 implementation checkpoint (historical)
- Base commit: `53c4b92d36ef9e3d4cebb7d5a11fed2fa2cfbc7d`; implementation commit: `6d784f318604c63354cdc5e5b367c2dcea0f60c0`.
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
- Correction: explicitly provision the SDK command-line tools with `android-actions/setup-android@v3` before sdkmanager; do not rely on preinstalled runner tools. Resolved by the subsequent passing run below.

## P1 visual correction — same batch
- [Run 36306734372](https://github.com/abo-sultann/DarbakOS/actions/runs/36306734372), commit `695c614a4f8535ab296f25173f2af6fcaed446e8`: build/lint, all 4 instrumentation tests, UI navigation and cold restart passed. Six screenshots inspected; no clipped text, but the RTL speed-card margin landed on the outer left edge and removed the intended inter-card gap.
- Corrected that margin from end to start on the explicitly RTL card; strengthened the existing 1024x600 test to assert the 16dp inter-card gap. Revalidation passed at final code `72fde4a` (see final acceptance above).
- Preliminary measurements: APK 18,217 bytes; launch TotalTime 296ms; process PSS 8,713KB (about 8.51MiB). These are x86 emulator observations, not T3 performance claims.
- Crash buffer empty; no app ANR detected. CPU snapshot covered an earlier boot interval with no app process, so app CPU is NOT MEASURED (not 0%).
- Lint had no errors; warnings concern pinned test-dependency versions, deliberate landscape, simple nested weights/baseline alignment/overdraw and spare color tokens. No runtime dependencies/native libraries in the APK.

## Test stages
1. Laptop/emulator initial validation
2. Darbak Test Station (Acer/BlissOS) where useful
3. Real Allwinner T3 commissioning/validation
4. Stable acceptance

For every test record: date, commit, environment, API/ABI/resolution, scenario, result, RAM/CPU where available, crash/ANR/log notes, screenshots/report reference, and follow-up.
