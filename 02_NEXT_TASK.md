# P3 Core State publish/subscription verification — COMPLETE / STOP

## Closure — 2026-09-28
Code31ce9bc passed run36361519633: Build/Lint,14/14 API25 tests (all8 prior +6 focused),6/6 quick-action returns and Cold Restart/no-autoplay. Revision/publish/snapshot/listener identity, synchronous publishing-thread delivery, duplicate/remove/null/reset semantics verified. Home/Apps/restart images unchanged; resource deltas/evidence recorded in TEST_RESULTS.md. No app fix needed.

STOP. No Guardian, logging/reporting, source adapters or further P3 work/batch.

## Original start point
P3 Core State foundation passed. Main now adds the smallest next Core increment: immutable state revisions plus an in-process publish/listener mechanism. It is infrastructure only; the final P2 UI is intentionally not bound to synthetic state and no source/backend is connected.

## Goal
Verify deterministic publish/snapshot/listener semantics on API25 without adding background execution or regressing P2.

## Required verification
- Build/Lint and all existing API25/1024x600 tests.
- Add focused Core tests for: revision increments; publish updates snapshot; listener receives the exact published immutable state; duplicate listener registration does not duplicate callbacks; removeListener stops callbacks; null publish is ignored; cold reset returns revision 0/unavailable/stopped.
- Check listener callback execution is synchronous on the publishing thread; do not add threads/Handler/Executor.
- Regress Cold Restart, Home/Apps/RTL/quick actions/Back/Home/no-autoplay and truthful unavailable UI.
- Confirm no Service/permission/dependency/native library/disk/network/sensor/background execution was introduced.
- Record APK/PSS delta and crash/ANR observations.
- Fix only defects proven by this increment; update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md.
- Commit + push, then STOP.

## Constraints
Do not bind UI to fake/synthetic state. Do not start Guardian, logging/report export, services, source adapters, OsmAnd/GPS/Trip, playback, Vehicle sources, PackageManager backend, firmware/root/MCU/70mai or T3 work.

## Finish definition
Core publish/subscription semantics are verified, lightweight and API25-safe; P2 remains visually/behaviorally unchanged; checkpoint in GitHub then STOP.
