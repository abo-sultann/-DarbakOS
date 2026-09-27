# Changelog

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
