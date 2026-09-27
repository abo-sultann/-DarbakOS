# Changelog

## 2026-09-27 — P1 implementation checkpoint (verification pending)
- Added separate Arabic RTL/landscape TEST shell with Home and placeholder navigation.
- Reused owner Launcher palette; reviewed Launcher.v2, TestStation and Dashline with license/compatibility notes.
- Added pinned Gradle/AGP, four Android instrumentation tests, emulator smoke/evidence workflow and build instructions.
- Local source checks passed; APK/runtime acceptance awaits remote CI because local Android tools/downloads are unavailable.

## P1 CI environment fix
### P1 visual verification
- API25 build and tests passed; screenshot review found reversed relative spacing on the speed card. Corrected RTL margin and added a measured 16dp gap assertion to the existing fit test.

- First remote run exposed missing sdkmanager on Ubuntu24.04; explicitly provision Android SDK tools. Still within the original P1 verification batch.

## Planning baseline v1.0
- Established Darbak OS dedicated project repository.
- Added Work master plan and continuation/checkpoint workflow.
- Locked target hardware/API constraints.
- Locked firmware/MCU/Golden Backup safety rules.
- Added laptop-first testing path.
- Added reuse-first reference policy.
