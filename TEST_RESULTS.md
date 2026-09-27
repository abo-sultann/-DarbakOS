# Test Results

No Darbak OS release is Stable. See the per-stage evidence below.

## 2026-09-27 — P3 Core State foundation: PASS / STOP

- Tested `6bcc62319d8690ee1316655de61bf15106669192`, [run36344308617](https://github.com/abo-sultann/DarbakOS/actions/runs/36344308617), job108690318096. This gate added tests only; no application defect found or application fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi,1GB RAM/software GPU: **8/8 instrumentation PASS** in3.657s, comprising all5 prior UI tests and3 focused CoreState tests.
- Core tests: coldBoot explicitly makes speed/navigation/vehicle UNAVAILABLE and mediaPlaying=false; singleton reads preserve snapshot identity, reset creates a new cold snapshot and leaves held immutable snapshots valid; fresh Activity resets core state, while recreation preserves the same snapshot and Home remains selected.
- Existing **6/6 actual-tap quick-action returns PASS**, all sections/Apps/RTL/Back/Home/recreation/fit preserved. Cold process restart lands on Home with honest unavailable/no-route/no-track/stopped states; settled state remains unchanged, app services0/media sessions0.
- Home/Apps screenshots visually reviewed and pixel-identical to P2 baseline27e0e79; cold-restart Home also pixel-identical. No clipping/overlap, temporary wording, fabricated values or autoplay.
- Source/manifest/build review and guards: no new permission, Android Service, receiver, runtime dependency or native library; APK contains no .so. Core uses immutable Java fields and synchronized in-memory snapshot/reset only, with no thread/timer/task, disk, network or playback work. P2 UI remains static and is not yet bound to a live source; this verifies only the requested internal foundation.

| Observation | P2 baseline27e0e79 | Core6bcc623 | Delta |
|---|---:|---:|---:|
| APK bytes | 20,925 | 22,607 | +1,682 (+8.04%) |
| Process PSS KB | 8,890 | 9,007 | +117 (+1.32%) |
| Launch TotalTime ms | 368 | 301 | -67 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

Separate single emulator observations, not controlled benchmarks: do not attribute PSS/startup variation solely to Core State. CPU performance is not established; raw CPU/gfx diagnostics retained, no 0% CPU claim or physical T3/ARMv7 extrapolation.

- Downloaded APK SHA256 verified: `276558073babaa7c20122c2cea4398318a8d72635b64f499d986fce6586a91d8`.
- Durable [evidence](docs/test-evidence/p3-core-state-20260927/): comparison JSON, Home/Apps/restart PNG, UI XML,8-test/6-flow outputs, no-autoplay/services/sessions/audio, lint, summary and raw resource/crash observations. Full artifact10939119217 expires2026-10-11.
- Existing REFERENCES/platform and test-harness choices reused; no component built from scratch in this verification gate. Local source guard, Python syntax, whitespace and exact unchanged resource/manifest/dependency diffs PASS.

**Disposition: Core State foundation complete; checkpoint and STOP. No Guardian, logging/reporting or other P3 work. No Stable/T3 acceptance.**

## 2026-09-27 — P2 final-product UI verification: PASS / STOP

- Owner instruction: current Darbak OS interface is the final-product UI, not a disposable prototype. Unconnected sources display truthful unavailable/stopped states. This supersedes earlier user-facing TEST-copy requirements; historical test records below remain historical.
- Tested `27e0e79236522cd175b49f38317c9b7b533e8122` in [run36343420707](https://github.com/abo-sultann/DarbakOS/actions/runs/36343420707), job108687799511.
- Proven defect in conversion: three Apps buttons were enabled despite having no listeners/backend. Disabled and visually dimmed those actions, retaining intended labels/layout and existing unavailable message. No backend added.
- Updated obsolete source/instrumentation/smoke TEST-copy requirements to exact final-state assertions. Added rejection of temporary wording in all string values and every captured visible text/accessibility description. Behavioral navigation, Back/Home, fit, RTL, cold restart and no-autoplay assertions retained.
- Build/Lint PASS: 0 errors,17 warnings. API25/x86,1024x600/160dpi,1GB RAM/software GPU: **5/5 instrumentation PASS** in3.214s (4 existing plus Apps fit/RTL/unavailable actions).
- **6/6 actual-tap quick-action returns PASS**. All sections/Settings visited. Apps actions appear in Apps only, labels `الأخيرة` / `المفضلة` / `إدارة التطبيقات`, each disabled and at least56px high. New instrumentation validates right-to-left order, full text/view fit and Return Home hiding Apps actions.
- Visual review: actual Home and Apps screenshots have no clipping/overlap, Arabic/RTL readable. Home shows unavailable speed, no active route with navigation unavailable, no selected media/stopped, and vehicle data unavailable. No fake turn/distance/time/track/position/normal-vehicle readings. No TEST/experimental/preview wording in UI or captured accessibility text.
- Cold Restart returns to Home; immediate/settled states remain unavailable/stopped. App services0 and MediaSessions0. No playback implementation or background behavior introduced. Crash buffer empty; no app ANR detected.
- APK20,925 bytes; downloaded SHA256 verified: `60e793776f91ae2e29334e8c1ddd74a48258fd8b00aeebf7d6e8767b770b0cab`.
- Durable [evidence](docs/test-evidence/p2-final-ui-20260927/): Home/Apps/restart PNG, all UI XML,5-test/6-flow results, no-autoplay/services/sessions/audio, summary, lint and runtime observations. Full artifact10939787622 expires2026-10-11.
- Local source guard, Python syntax and whitespace PASS. Reused existing Android Views/disabled-button capability and recorded REFERENCES/test harness; no new dependencies/services/backend.
- Final-product UI acceptance here is emulator-stage only; physical T3/ARMv7 and Stable release acceptance remain outstanding. Internal resource IDs/package/artifact names retaining test history are not user-visible labels.

**Disposition: complete, commit/push and STOP. No P3 or additional backend/batch.**

## 2026-09-27 — P2 stopped Media verification: PASS / STOP

- Tested `abc0b3dd756e39498d23df1597b0f0dd7ac8db04`, [run36342407385](https://github.com/abo-sultann/DarbakOS/actions/runs/36342407385), job108684931636. Application remains7ce4930; only smoke expectations/verification and documentation changed.
- Initial run36341691472 passed Build/Lint and4 tests, but failed obsolete `خامل` smoke expectation. Corrected to exact TEST track/stopped position; no application defect found or app change made.
- Build/Lint PASS: 0 errors,17 warnings (previous16 plus unused media_idle from this increment; no unrelated cleanup).
- API25/x86,1024x600/160dpi,1GB RAM/software GPU: **4/4 instrumentation PASS** in2.387s; text/view fit, RTL,16dp gap, navigation/recreation preserved.
- **6/6 actual-tap quick-action returns PASS**, correct destinations/selected tabs with Android Back and Return Home. Exact media, navigation TEST examples, unavailable speed/navigation, test-only normal vehicle, full stale warning and global TEST asserted after every return.
- **Cold Restart PASS:** force-stop from Settings then launch lands on Home; all labels checked immediately and after2s settling. Track/position unchanged, `متوقف (تجريبي)` remains visible. App service records0; app MediaSessions0. System telecom has one inactive unrelated session; not attributed to Darbak. Audio diagnostics retained.
- No-autoplay conclusion combines runtime observations with inspected MainActivity/manifest: static TextViews only, no playback/MediaSession/audio-focus code, service, receiver, permission or runtime dependency. This verifies the current static TEST shell, not a future media engine or physical T3.
- Visual review of actual Home and cold-restart Home: no clipping/overlap. `يا طريق • مقطع تجريبي` and `01:24 / 04:10 • متوقف (تجريبي)` fit fully; global test badge remains visible. Track y382–410, position/state y416–444, Media button y452–508; bottom navigation begins y528. All quick targets remain56px high.
- APK20,529 bytes; downloaded SHA256 verified: `3d7b23b011ecc3fe71e43b01f507912df49e9bc7cb6941fe4ad8d51627612b81`. Launch386ms (single emulator observation). Crash buffer empty; no app ANR found by smoke.
- Durable [evidence](docs/test-evidence/p2-media-20260927/) includes prior failure, Home/restart PNG/XML, returned Home XML, no-autoplay result and raw services/sessions/audio,6-flow/4-test results, lint, APK summary and launch/memory/crash. Full artifact10939271595 expires2026-10-11.
- Local source guard, Python syntax and whitespace PASS. Existing REFERENCES/platform/testing choices reused; no new component. No physical hardware/ARMv7/Stable acceptance.

**Disposition: complete, checkpoint and STOP. No P3 or next batch.**

## 2026-09-27 — Stopped Media verification setup (historical; resolved above)

- Mainea52d9b / implementation7ce4930: run36341691472 passed Build/Lint and4 instrumentation tests, but smoke stopped at outdated `خامل` expectation after Media changed to explicit stopped TEST preview.
- Actual screenshot reviewed: track and position/test qualifier fit; no clipping/overlap. No app defect found.
- Correct smoke expectation to exact TEST track/stopped position; preserve prior-state regressions. Add cold-restart/settled Home label checks and capture/assert no app ServiceRecord or MediaSession, plus audio diagnostics.
- MainActivity/manifest reviewed: static views only, no playback/audio-focus/MediaSession/service code or runtime dependency. Existing REFERENCES/platform/test workflow reused; no new component.
- Full rerun pending. Same bounded verification batch; finish evidence then STOP.

## 2026-09-27 — P2 navigation-card verification: PASS / STOP

- Verified code `b054d7d5a6e1d9f82d5fbe290a5cf415a418cbb3` using [run36325924001](https://github.com/abo-sultann/DarbakOS/actions/runs/36325924001), job108638552766. Existing successful run inspected, not rerun. Main2612b6a differs only in the task document; application/build/test input diff is empty.
- Build/Lint PASS: 0 errors,16 warnings (previous15 plus unused navigation_detail after replacement; no unrelated cleanup).
- API25/x86,1024x600/160dpi,1GB RAM/software GPU: **4/4 instrumentation PASS**,2.817s, including text/view fit,16dp gap, RTL, section navigation and recreation.
- **6/6 actual-tap quick-action round trips PASS**: Map/Media/Vehicle via Android Back and Return Home. Existing navigation/Settings/cold restart smoke passed. Touch targets remain56px high.
- Actual Home screenshot reviewed: no clipping/overlap. Maneuver is bold24sp; ETA/distance20sp below; gold unavailable state clearly separated. Maneuver bounds y195–228, ETA y234–262, unavailable y270–298, all inside the navigation card y88–324.
- Semantic check: `بعد 800 م • انعطف يمينًا (تجريبي)` and `12 د • 7.4 كم (تجريبي)` each carry their own test qualifier; global `نسخة اختبار • بيانات تجريبية` and `الملاحة • غير متاحة` remain visible. These are static TEST examples, not live guidance.
- Post-run assertions against eight captured Home UI trees (launch, six quick-action returns, cold restart) passed for both navigation examples, unavailable navigation/speed, idle media, test-only normal vehicle summary, full stale warning and global TEST badge. Retained JSON distinguishes this evidence inspection from a new runtime test.
- Crash buffer empty; no app ANR found. APK20,213 bytes; downloaded SHA256 verified: `8c838a340926fe45edee2ea5364d993fa8177bed941f963b243d5dacbb07c166`. Launch408ms, single emulator observation only.
- Durable [evidence](docs/test-evidence/p2-navigation-20260927/): Home PNG, initial/return/restart UI XML, semantic-review JSON,6-flow JSON,4-test output, summary, lint XML, launch/memory/crash. Full artifact10933768031 expires2026-10-11.
- Source guard and whitespace checks PASS. No application defect found, no app/test/build change needed. Existing REFERENCES/platform choices reused; no new component or dependency.

**Disposition: complete, checkpoint and STOP. No P3/next batch or T3/Stable acceptance.**

## 2026-09-27 — P2 quiet Home summary: PASS / STOP

- Tested code `9ac0b10586a1fa76a165717a064b90a69d3f74e3`, [run36324401688](https://github.com/abo-sultann/DarbakOS/actions/runs/36324401688), job108634243574.
- Proven regression: initial d75e683 run36324136031 failed the existing fit test (1/4 failures), with the vehicle title outside its card after the added summary. Original failure output retained.
- Minimal fix: merge redundant title/summary into one platform TextView. Qualify normal directly as `السيارة ✓ طبيعية (تجريبي)` so the affirmative checkmark does not stand alone as a real vehicle assessment. Global `نسخة اختبار • بيانات تجريبية` and full gold stale warning remain visible. No data integration or new component/dependency.
- Build and Lint PASS: 0 errors, 15 warnings (previous14 plus unused home_no_alerts from this increment; no unrelated cleanup).
- API25/x86, 1024x600/160dpi, 1GB RAM/software GPU: existing instrumentation **4/4 PASS** in2.602s, including all-view/text fit and16dp gap.
- Actual UIAutomator-bound taps: three quick actions × Android Back/Return Home = **6/6 PASS**. Correct destination/selected tab and unavailable speed/navigation, idle media, stale warning, global TEST and qualified summary asserted after each return. Existing navigation/Settings/recreation/cold restart checks pass.
- Visual/semantic review of actual Home PNG and UI XML: no clipping, overlap or ellipsis. Summary occupies y350–378, full stale warning y386–438, vehicle button y446–502, bottom navigation begins y528. TEST badge is legible at top. Summary explicitly describes a test state, never a live vehicle assessment; no numeric vehicle readings or connectivity claim.
- All quick-action targets retain56px height; map280px wide, media/vehicle292px. Crash buffer empty; no app ANR detected.
- APK19,837 bytes, downloaded SHA256 verified: `a142b9a2182206dd30c057dfe70ca474713c4a17357b1ef874bb84faf5ef3c99`. Launch277ms (single emulator observation, not a benchmark).
- Durable [evidence](docs/test-evidence/p2-quiet-home-20260927/): before-failure output, final PNG/XML, lint XML, four-test output, six-flow JSON, summary, launch/memory/crash. Full artifact10933013785 expires2026-10-11.
- Local source guard, Python syntax and whitespace PASS. No physical T3/ARMv7/Test Station validation or Stable claim.

**Disposition: verification batch complete; commit/push and STOP. No next batch/P3.**

## 2026-09-27 — Quiet Home verification setup (historical; resolved above)

- Existing run36324136031 at d75e683 passed Build/Lint but failed 1/4 instrumentation tests: vehicle title clipped (Rect360,330–652,363) after adding the summary. Smoke stopped before screenshot/navigation; no pass claimed.
- Minimal correction: replace the redundant vehicle title plus summary with one summary line; explicitly qualify normal as `(تجريبي)` on that line, retaining the global TEST badge and full stale-source warning. No new component/dependency.
- Reuse existing platform TextView/layout and recorded REFERENCES decisions. Extend existing Home assertions to require the qualified summary on launch and all six quick-action returns.
- Full Build/Lint/API25/1024x600 rerun and visual review pending. Same verification batch only; STOP after checkpoint.

## 2026-09-27 — P2 Home quick-actions verification: PASS / STOP

Tested `cf062d6dfcee442755751e096acf6623fe29c8f8` in [run 36323597028](https://github.com/abo-sultann/DarbakOS/actions/runs/36323597028), job108632008811.
This batch adds targeted tests to the existing smoke script only. Application code remains the implementation from `b87ba33`; no app defect was found.

- **Build/Lint:** passed; 0 lint errors / 14 existing warnings (same categories as the preceding gate).
- **Existing instrumentation:** 4/4 passed in2.824s on API25/x86, 1024x600/160dpi, 1GB emulator RAM/software GPU. Includes RTL/fit/16dp gap, navigation, recreation and Home behavior.
- **New actual-touch verification:** each quick action tapped using UIAutomator bounds, exact section title/selected tab asserted, then Android Back and Return Home tested separately. **6/6 round trips passed**; Home unavailable/idle/stale labels and selected Home tab rechecked after every return.

| Button | Destination | Measured target | Android Back | Return Home |
|---|---|---|---|---|
| quick_map | الخريطة | 280x56px | PASS | PASS |
| quick_media | الوسائط | 292x56px | PASS | PASS |
| quick_vehicle | السيارة | 292x56px | PASS | PASS |

- **Screenshot:** final Home1024x600 inspected against the reviewed pre-test Home; pixel-identical. No clipping/overlap, spacing retained, all targets inside screen and at least56px high. Vehicle stale label is fully visible across two lines; no numeric stale reading is displayed live.
- **Regression smoke:** existing nav sections, Settings and cold restart still passed. Crash buffer empty; no app ANR detected during run.
- **APK:**19,557 bytes; downloaded SHA256 verified: `a3d330a43e291e3daa9fc2d904e4fb59a0b9c2547045967040d8535c8d5b3a14`.
- **Observations:** launch TotalTime399ms/WaitTime408ms; PSS8,776KB (~8.57MiB). Single emulator observations; no real-device or CPU-performance claim.
- **Evidence:** [p2-quick-actions-20260927](docs/test-evidence/p2-quick-actions-20260927/) retains Home PNG/XML,6-round-trip result JSON, summary, instrumentation, launch/memory/crash output. Full artifact10933750584 expires2026-10-11 and includes destination/return UI XML and logs.
- **Local checks:** source constraints, Python syntax and whitespace passed. No app/build dependency changes, hardware testing, new features or next phase.

**Disposition:** current verification batch complete; checkpoint and STOP. Not Stable/T3 acceptance.

## 2026-09-27 — Home quick-actions verification setup (historical)
- Main `d80f4fa6a247820826dd67ef59f5dbb75f313f45`; application implementation `b87ba3371d2d9c2aa3655c2ec7d8859f7519f24a`.
- Existing run https://github.com/abo-sultann/DarbakOS/actions/runs/36323265109 passed Build/Lint and the four existing tests, but did not tap the new quick actions.
- Actual Home PNG/UI XML inspected: no clipping/overlap; quick_map 280x56px, quick_media 292x56px, quick_vehicle 292x56px. Unavailable/idle/stale labels remain visible.
- Extend the existing emulator smoke only: tap each quick action, verify exact destination/selected tab, return with Android Back and Return Home, and recheck Home labels. New run pending; no app code changed.
- Scope remains verification-only. Finish evidence/checkpoint, then STOP.

## 2026-09-27 — P2 verification-only gate: PASS / STOP

Verified existing implementation `682d895c9e08ac6a4d3c9eacce69853db41cd6b2` using
[successful run 36308631278](https://github.com/abo-sultann/DarbakOS/actions/runs/36308631278), job `108590098596`.
At review, main was `17fe772379ac00e2848e5e71487eee36d21af72c`. Its only difference from the tested commit is
`02_NEXT_TASK.md`: `git diff --exit-code 682d895 HEAD -- app scripts .github gradle gradlew gradlew.bat build.gradle settings.gradle gradle.properties` passed.
The completed CI run was inspected, not rerun unnecessarily; downloaded evidence was checked against its exact code/hash.

| Required check | Verified result |
|---|---|
| Build/Lint | Successful workflow build of app/test APKs; lint XML: 0 errors, 14 warnings |
| Runtime | API25/default/x86, 1024x600, 160dpi; existing workflow's 1GB RAM/software GPU configuration |
| Existing instrumentation | 4/4 passed in 2.706s; launch/RTL, destinations/Back, recreation/Home, measured fit and 16dp gap |
| Navigation/cold restart | Existing UI-driven smoke passed, with crash/ANR checks |
| Home screenshot | Inspected actual emulator PNG: no clipped text, overlap or lost card spacing; vehicle text fits on two lines |
| Speed | Visible dash and disconnected test-source label; accessibility says speed unavailable |
| Navigation | Visible `الملاحة • غير متاحة` |
| Media | Visible `لا يوجد تشغيل • خامل` |
| Vehicle stale | Visible `آخر قراءة تجريبية قديمة • لا تعرض كقراءة حية`; no numeric vehicle reading shown as live |
| Test provenance | Global test-data badge visible; state text corroborated against captured Home UI XML |
| Crash/ANR | Empty crash buffer; smoke found no app ANR during the run |
| APK/hash | 18,793 bytes; SHA256 `f325038a164061b0b2e7c749108be8fcec7718d8a75d8533ba8a135df4730c8f` verified locally |
| Observations | Launch TotalTime408ms/WaitTime415ms; PSS8,610KB (8.41MiB), single emulator snapshots only |
| Changes needed | None to application/build/tests: no defect proven in this increment |

Lint warnings: 4 pinned test-library version suggestions, fixed landscape, 2 baseline-alignment suggestions,
nested weights, background overdraw, and 5 unused resources (including the 3 new generic state strings).
These do not block the requested gate; no cleanup or feature work was added. CPU performance and real T3/ARMv7/TestStation behavior are not established.

Durable evidence: [p2-verification-20260927](docs/test-evidence/p2-verification-20260927/), including Home PNG/XML,
explicit verification checks, instrumentation result, APK summary, launch/memory and empty crash output.
Full artifact: `10927543936`, named `DarbakOS-P1-TEST-682d895c9e08ac6a4d3c9eacce69853db41cd6b2` (existing workflow name retained), expires 2026-10-11.
Local source guard and documentation whitespace checks passed. No hardware/system work, next P2 increment or P3 was started.
**Disposition: checkpoint this verification result, then STOP. Not a Stable/T3 acceptance.**

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
