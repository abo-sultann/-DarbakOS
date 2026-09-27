# Changelog

## 2026-09-27 — Navigation-card verification closed / STOP
- Verified existing b054d7d Build/Lint,4 API25 tests and6 quick-action round trips from run36325924001; no application change needed.
- Reviewed Home1024x600: no clipping/overlap; maneuver and ETA/distance each explicitly test-only, navigation unavailable visible.
- Rechecked all labels in eight captured Home states, retained evidence and updated status/results/gate. No next batch/P3.

## 2026-09-27 — Quiet Home verification closed / STOP
- Fixed proven clipping introduced by the new vehicle summary by merging the redundant title/summary.
- Labeled normal explicitly `(تجريبي)` beside the status, preserving global TEST and full stale warning.
- Extended existing Home assertions; code9ac0b10 passed Build/Lint,4 API25 tests and6 quick-action returns. Actual screenshot reviewed at1024x600.
- Saved failure/final evidence and updated state/results/gate. No next batch or P3.

## 2026-09-27 — Home quick-actions verification closed / STOP
- Added targeted emulator smoke checks for three Home quick actions, correct destination/selected tab, Android Back and Return Home, with label/56px target assertions.
- Code `cf062d6` passed Build/Lint,4 existing API25 tests and6 actual-tap round trips at1024x600.
- Reviewed final Home image; no clipping/overlap or app defect found. App code unchanged.
- Preserved evidence and updated state/gate/results. No additional P2 increment or P3 started.

## 2026-09-27 — P2 verification-only checkpoint / STOP
- Verified the existing Home unavailable/idle/stale labels from code `682d895`; main `17fe772` has identical application/build/test inputs.
- Reviewed successful Build/Lint, 4 API25/1024x600 tests, navigation/restart smoke and the actual Home screenshot.
- Confirmed no clipping/overlap and no stale vehicle value presented as live; no application fix was necessary.
- Preserved evidence and updated current status, gate closure and test results. No subsequent task or phase started.

## 2026-09-27 — P1 closed / initial emulator validation passed
- Final code `72fde4a` passed build/lint, four Android tests, UI navigation and cold restart on API25/x86 at 1024x600.
- Fixed RTL dashboard spacing after screenshot review and verified the exact 16dp gap.
- Recorded final APK hash/size, launch/PSS snapshots, CPU measurement limitation and clean crash/ANR result.
- Preserved selected evidence/screenshots, updated official status and defined only the next small P2 task.
- No T3/firmware/system work; no Stable declaration; no next batch started.

## 2026-09-27 — P1 implementation checkpoint (verification pending)
- Added separate Arabic RTL/landscape TEST shell with Home and placeholder navigation.
- Reused owner Launcher palette; reviewed Launcher.v2, TestStation and Dashline with license/compatibility notes.
- Added pinned Gradle/AGP, four Android instrumentation tests, emulator smoke/evidence workflow and build instructions.
- Local source checks passed; APK/runtime acceptance awaits remote CI because local Android tools/downloads are unavailable.

## P1 visual verification
- API25 build and tests passed; screenshot review found reversed relative spacing on the speed card. Corrected RTL margin and added a measured 16dp gap assertion to the existing fit test.

## P1 CI environment fix
- First remote run exposed missing sdkmanager on Ubuntu24.04; explicitly provision Android SDK tools. Still within the original P1 verification batch.

## Planning baseline v1.0
- Established Darbak OS dedicated project repository.
- Added Work master plan and continuation/checkpoint workflow.
- Locked target hardware/API constraints.
- Locked firmware/MCU/Golden Backup safety rules.
- Added laptop-first testing path.
- Added reuse-first reference policy.
