# Next Task — P3 Guardian health-state foundation verification

## Start point
Core State/cold-reset gates are closed. Main now contains the first Guardian foundation primitive: GuardianState. It is deliberately pure in-process state only; it does not monitor, schedule, restart, log, persist or touch Android services.

## Goal
Verify the Guardian health-state model is deterministic, immutable and essentially zero-runtime-cost before any supervisor behavior is introduced.

## Required verification
- Build/Lint and every existing API25/1024x600 test.
- Add focused unit/instrumentation coverage proving:
  - coldBoot = UNKNOWN revision0;
  - HEALTHY/DEGRADED/FAILED transitions increment revision exactly once;
  - retained previous instances remain immutable;
  - same-state transition returns the same object and does not increment revision;
  - null transition returns same object and does not increment revision;
  - recovery FAILED -> HEALTHY is representable without hidden action.
- Regress all17 existing Core/reset tests and6 quick-action navigation returns.
- Confirm Home/Apps/Cold Restart screenshots and final P2 behavior remain unchanged.
- Confirm GuardianState introduces no Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution.
- Record APK/PSS delta and crash/ANR observations.
- Fix only defects proven by this increment.
- Update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md; Commit + Push; then STOP.

## Constraints
This is not a watchdog yet. Do not add Guardian service, heartbeat polling, timers, restart/self-healing actions, logging/report export, persistence, UI, notifications, source adapters, OsmAnd/GPS/Trip, playback, Vehicle sources, firmware/root/MCU/70mai or T3 work.

## Finish definition
Guardian health-state primitive is verified on API25/1024x600 with all prior behavior intact and no background cost; evidence is checkpointed in GitHub, then STOP.
