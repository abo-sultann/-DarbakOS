# Next Task — P3 Guardian passive Supervisor verification

## Start point
Guardian recovery-policy is closed at 57/57 API25 tests. Main now adds GuardianSupervisor: a caller-driven, passive orchestration step that performs one consistent chain: GuardianSnapshot.capture -> GuardianAssessment.from -> GuardianRecoveryPolicy.recommend.

## Goal
Verify the first Supervisor layer composes the already-proven Guardian primitives correctly without becoming a watchdog, scheduler, service or recovery executor.

## Required verification
- Build/Lint and every existing API25/1024x600 test.
- Add focused coverage proving:
  - null registry produces a valid UNKNOWN snapshot/assessment and DIAGNOSE recommendation;
  - cold registry produces the same conservative UNKNOWN result;
  - HEALTHY/DEGRADED/FAILED/UNKNOWN states flow exactly from snapshot through assessment into policy;
  - all1024 health combinations across escalation levels0..5 produce a Result whose snapshot, assessment and plan agree exactly;
  - negative and high escalation levels preserve the already-verified policy boundaries;
  - evaluation never mutates registry health/revisions;
  - retained Result remains immutable after later registry updates/reset;
  - a later evaluation reflects the new registry state while the older Result remains unchanged;
  - repeated evaluation of unchanged registry is value-consistent and does not increment revisions;
  - concurrent caller-driven evaluations/registry updates cannot produce internally split Result state.
- Preserve all57 prior tests and6 quick-action navigation returns.
- Confirm Home/Apps/Cold Restart screenshots and final P2 behavior remain unchanged.
- Confirm production code adds no Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/scheduled polling/listener/disk/network/sensor/restart/recovery execution.
- Record APK/PSS delta and crash/ANR observations.
- Fix only defects proven by this increment.
- Update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md; Commit + Push; then STOP.

## Constraints
Passive Supervisor composition only. Do not add watchdog loops, heartbeat/staleness clocks, timers, Android Service, automatic listeners, actual recovery/restart/Safe Mode/rollback, logging/report export, persistence, UI/notifications, source adapters, OsmAnd/GPS/Trip, playback, Vehicle sources, firmware/root/MCU/70mai or T3 work.

## Finish definition
One caller-driven Guardian Supervisor evaluation is proven internally consistent and side-effect-free on API25/1024x600, all previous behavior remains intact, evidence is checkpointed in GitHub, then STOP.
