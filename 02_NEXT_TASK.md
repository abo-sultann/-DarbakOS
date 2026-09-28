# Next Task — P3 cold-reset publication COMPLETE / STOP

## Closure — 2026-09-28
Verified code `cdc64b3b201e7e6f2de570a0c82b099114e186b5`: Build/Lint PASS,17/17 API25 tests and6/6 quick-action returns. Three focused reset/listener tests added; all14 prior tests retained. Home/Apps/Cold Restart unchanged; no application defect found. Evidence: `docs/test-evidence/p3-cold-reset-20260928/` and TEST_RESULTS.md.

This batch is complete. STOP. No next task is assigned; do not start Guardian, logging or any other part. Original scope below is retained for audit.

## Start point
Core publish/subscription passed. Review found one lifecycle consistency gap before Guardian/logging: resetForColdBoot replaced the snapshot silently, so an already-registered in-process consumer could retain stale state. Main now routes cold reset through publish(DarbakState.coldBoot()). No UI/backend/service change.

## Goal
Verify cold reset is an ordinary truthful state publication without changing the accepted P2 UI or adding background work.

## Required verification
- Build/Lint and all existing API25/1024x600 tests.
- Add/adjust focused Core tests proving: reset publishes exactly one coldBoot state to each unique registered listener; published reset is the same object returned by snapshot; reset state is revision0/all unavailable/media stopped; removed listener receives no reset; repeated reset is deterministic; callback remains synchronous on caller thread.
- Preserve all prior revision/publish/listener/null semantics and existing test strength.
- Regress Cold Restart, Home/Apps/RTL/quick actions/Back/Home/no-autoplay and truthful unavailable UI.
- Confirm no Service/permission/dependency/native library/disk/network/sensor/thread/background execution was introduced.
- Record APK/PSS delta and crash/ANR observations.
- Fix only defects proven by this increment; update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md.
- Commit + push, then STOP.

## Constraints
Do not start Guardian, logging/report export, source adapters, UI binding to synthetic state, services, OsmAnd/GPS/Trip, playback, Vehicle sources, PackageManager backend, firmware/root/MCU/70mai or T3 work.

## Finish definition
Cold reset publication semantics are verified on API25/1024x600, prior Core/P2 behavior remains intact, evidence is checkpointed in GitHub, then STOP.
