# Next Task — P3 Guardian component-registry verification

## Start point
Guardian health-state primitive passed. Main now adds GuardianRegistry: a small in-process owner for explicit health of CORE, HOME, NAVIGATION, MEDIA and VEHICLE. The registry stores only health supplied by future monitors; it does not poll, infer, schedule, persist, restart or touch Android services.

## Goal
Verify the Guardian component registry is deterministic, isolated per component and essentially zero-background-cost before any watchdog behavior is introduced.

## Required verification
- Build/Lint and every existing API25/1024x600 test.
- Add focused coverage proving:
  - cold boot creates UNKNOWN revision0 for every component;
  - updating one component does not mutate any other component;
  - changed health increments only that component revision exactly once;
  - same/null health update preserves identity/revision;
  - resetForColdBoot restores every component to a fresh UNKNOWN revision0 state;
  - null component lookup/update is harmless and does not alter registry contents;
  - FAILED -> HEALTHY recovery is representable per component without hidden action.
- Preserve all22 previous Core/Guardian tests and6 quick-action navigation returns.
- Confirm Home/Apps/Cold Restart screenshots and final P2 behavior remain unchanged.
- Confirm no Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution was introduced.
- Record APK/PSS delta and crash/ANR observations.
- Fix only defects proven by this increment.
- Update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md; Commit + Push; then STOP.

## Constraints
This is still not a watchdog. Do not add Guardian service, heartbeat polling, timers, listener callbacks, restart/self-healing actions, logging/report export, persistence, UI, notifications, source adapters, OsmAnd/GPS/Trip, playback, Vehicle sources, firmware/root/MCU/70mai or T3 work.

## Finish definition
Guardian component registry is verified on API25/1024x600 with all prior behavior intact and no background execution; evidence is checkpointed in GitHub, then STOP.
