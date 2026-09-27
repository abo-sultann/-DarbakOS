# P3 Core State foundation verification — COMPLETE / STOP

## Closure — 2026-09-27
Code6bcc623 passed run36344308617: Build/Lint,8/8 API25 tests (all5 UI +3 Core),6/6 quick-action returns and Cold Restart/no-autoplay. Core coldBoot/reset/snapshot/recreation semantics verified. Home/Apps/restart images match P2 pixel-for-pixel. Resource deltas and crash observations recorded in TEST_RESULTS.md; no app fix needed.

STOP. No Guardian, logging/reporting, another P3 component or additional batch.

## Original start point
P2 final-product UI is closed and verified. P3 begins with the smallest internal foundation: DarbakState + CoreStateStore. Cold boot initializes truthful unavailable/stopped state. This adds no Android Service, disk logging, sensor, network, playback, Guardian recovery or backend.

## Goal
Verify the core-state owner is API25-safe, lightweight and does not regress the final P2 UI.

## Required verification
- Build/Lint and all existing API25/1024x600 tests.
- Add focused tests for DarbakState.coldBoot and CoreStateStore reset/snapshot semantics without weakening existing UI assertions.
- Cold Restart must still land on Home with speed/navigation/vehicle unavailable and media stopped; no fabricated live state.
- Confirm no new Android Service/permission/runtime dependency/native library/background execution was introduced.
- Regress Home, Apps, RTL, quick actions, Back/Home and no-autoplay.
- Record resource/APK delta and crash/ANR observations.
- Fix only defects proven by this P3 increment; update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md.
- Commit + push, then STOP.

## Constraints
Do not add Guardian recovery, file logging/report export, services, sensors, OsmAnd/GPS/Trip, playback, Vehicle sources, PackageManager backend, new dependencies, firmware/root/MCU/70mai or T3 work in this gate.

## Finish definition
P3 Core State foundation is verified on API25/1024x600 with P2 UI unchanged and truthful cold-boot semantics, checkpointed in GitHub. No Stable/T3 acceptance is implied.
