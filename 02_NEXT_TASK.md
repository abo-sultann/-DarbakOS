# Next Task — P3 Guardian aggregate-policy COMPLETE / STOP

## Closure — 2026-09-28
Verified `e42f2d6d9355afabd6c4d7830c4182dadbed2685`: Build/Lint PASS,36/36 API25 tests (all29 prior +7 policy),6/6 navigation returns. All1024 combinations/precedence/recovery/nonmutation verified; final P2 preserved; no production defect found. Evidence: `docs/test-evidence/p3-guardian-aggregate-20260928/` and TEST_RESULTS.md.

STOP. No next task is assigned. Do not start watchdog, logging or any other part. Original scope retained below for audit.

## Start point
Guardian component registry passed29/29 API25 tests. Main now adds GuardianPolicy: a pure deterministic aggregation rule over the explicit component states already held by GuardianRegistry. It performs no monitoring, timing, callbacks, persistence, restart or Android background work.

## Goal
Verify one truthful overall Guardian health can be derived from component health without hidden actions or resource cost.

## Required verification
- Build/Lint and every existing API25/1024x600 test.
- Add focused tests for the aggregate precedence and boundaries:
  - null registry -> UNKNOWN;
  - all UNKNOWN -> UNKNOWN;
  - all HEALTHY -> HEALTHY;
  - any FAILED -> FAILED, regardless of other states;
  - no FAILED + any DEGRADED -> DEGRADED, including when another component is UNKNOWN;
  - no FAILED/DEGRADED + any UNKNOWN -> UNKNOWN;
  - recovery from FAILED/DEGRADED to all HEALTHY -> HEALTHY;
  - aggregation must not mutate any component state/revision or registry contents.
- Exercise every component as the sole FAILED, DEGRADED and UNKNOWN source.
- Preserve all29 previous tests and6 quick-action navigation returns.
- Confirm Home/Apps/Cold Restart screenshots and final P2 behavior remain unchanged.
- Confirm no Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution was introduced.
- Record APK/PSS delta and crash/ANR observations.
- Fix only defects proven by this increment.
- Update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md; Commit + Push; then STOP.

## Constraints
This is policy only, not a watchdog. Do not add polling/heartbeat/timers/listeners/restart/self-healing, Guardian Service, logging/report export, persistence, UI/notifications, source adapters, OsmAnd/GPS/Trip, playback, Vehicle sources, firmware/root/MCU/70mai or T3 work.

## Finish definition
Guardian aggregate health policy is fully verified on API25/1024x600, all previous behavior remains intact, evidence is checkpointed in GitHub, then STOP.
