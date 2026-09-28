# Next Task — P3 Guardian assessment verification

## Start point
Guardian snapshot is verified and race-safe. Main now adds GuardianAssessment: an immutable diagnostic classification derived from one GuardianSnapshot. It groups CORE/HOME/NAVIGATION/MEDIA/VEHICLE into healthy/degraded/failed/unknown sets and exposes counts/membership only. It performs no monitoring, action, persistence, UI or Android background work.

## Goal
Verify Guardian can turn one frozen snapshot into a stable diagnostic assessment without mutating snapshot/registry state or introducing recovery behavior.

## Required verification
- Build/Lint and every existing API25/1024x600 test.
- Add focused coverage proving:
  - null snapshot => all5 components UNKNOWN, overall UNKNOWN;
  - cold snapshot => all5 UNKNOWN/revision0 classification;
  - every component is classified into exactly one bucket;
  - healthy/degraded/failed/unknown counts always sum to5;
  - all HEALTHY => healthyCount5/overall HEALTHY;
  - mixed states preserve exact per-component membership and snapshot aggregate precedence;
  - every component independently works as HEALTHY, DEGRADED, FAILED and UNKNOWN;
  - retained GuardianAssessment remains unchanged after registry update/reset/new snapshot;
  - assessment creation does not alter any GuardianState health/revision or registry contents;
  - repeated assessment from the same snapshot is value-consistent;
  - null component membership queries return false and never mutate state.
- Preserve all43 prior tests and6 quick-action navigation returns.
- Confirm Home/Apps/Cold Restart screenshots and final P2 behavior remain unchanged.
- Confirm no Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution was introduced.
- Record APK/PSS delta and crash/ANR observations.
- Fix only defects proven by this increment.
- Update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md; Commit + Push; then STOP.

## Constraints
Assessment only. Do not add watchdog/polling/heartbeat/timers/listeners/restart/self-healing actions, Guardian Service, logging/report export, persistence, UI/notifications, source adapters, OsmAnd/GPS/Trip, playback, Vehicle sources, firmware/root/MCU/70mai or T3 work.

## Finish definition
Guardian diagnostic assessment semantics are fully verified on API25/1024x600 with all prior behavior intact and zero background execution; evidence is checkpointed in GitHub, then STOP.
