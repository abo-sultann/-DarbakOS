# Next Task — P3 Guardian recovery-policy verification

## Start point
Guardian assessment passed50/50 API25 tests. Main now adds GuardianRecoveryPolicy: a pure staged recommendation derived from one frozen GuardianAssessment plus an explicit escalation level. It encodes the approved recovery ladder without executing any action.

## Goal
Verify the recovery ladder is deterministic, conservative and cannot trigger hidden work before Guardian Supervisor exists.

## Required verification
- Build/Lint and every existing API25/1024x600 test.
- Add focused coverage proving:
  - null assessment is treated as UNKNOWN and recommends DIAGNOSE only;
  - HEALTHY always recommends NONE with zero targets at every escalation level;
  - UNKNOWN always recommends DIAGNOSE and targets exactly unknown components;
  - DEGRADED recommends DIAGNOSE at level0, LIGHT_REPAIR at level>=1, and never escalates to restart/fallback/safe mode by itself;
  - FAILED follows exactly: level0 DIAGNOSE, level1 LIGHT_REPAIR, level2 RESTART_COMPONENT, level3 FALLBACK_STABLE, level>=4 SAFE_MODE;
  - negative escalation is clamped to level0;
  - plan targets exactly the relevant failed/degraded/unknown components and null target queries are false;
  - every component independently works as the sole failed/degraded/unknown target;
  - all1024 health combinations across escalation levels0..5 produce deterministic policy output;
  - creating plans never mutates assessment/snapshot/registry health or revisions;
  - retained Plan remains immutable after later registry updates/reset/new assessments.
- Preserve all50 previous tests and6 quick-action navigation returns.
- Confirm Home/Apps/Cold Restart screenshots and final P2 behavior remain unchanged.
- Confirm no Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution was introduced.
- Record APK/PSS delta and crash/ANR observations.
- Fix only defects proven by this increment.
- Update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md; Commit + Push; then STOP.

## Constraints
Policy only. Do not execute recovery, restart components/apps, enter Safe Mode, rollback versions, add watchdog/polling/heartbeat/timers/listeners, Guardian Service, logging/report export, persistence, UI/notifications, source adapters, OsmAnd/GPS/Trip, playback, Vehicle sources, firmware/root/MCU/70mai or T3 work.

## Finish definition
Guardian staged recovery recommendation is fully verified on API25/1024x600, conservative boundaries are proven, all previous behavior remains intact, evidence is checkpointed in GitHub, then STOP.
