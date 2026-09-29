# Next Task — P3 Guardian Monitoring Foundation COMPLETE / STOP

## Closure — 2026-09-29
Verified `0293c4e9cef7f102cacbc99c0905f9b3c95379c7`, run36573126651: Build/Lint PASS; focused Java10/10 and API25 focused16/16 PASS; then exactly one API25/1024x600 regression74/74 +6/6 navigation returns PASS. Fixed only proven new-bundle Snapshot API incompatibility and config overflow. Final P2 screenshots unchanged, no autoplay/crash/ANR. Evidence: `docs/test-evidence/p3-guardian-monitoring-20260929/`.

The latest owner instruction not to rerun unaffected giant tests takes precedence over the original all65 wording below:58 prior tests rerun;7 giant proofs reused from the prior65/65 checkpoint with12 checked source fingerprints. All prior tests preserved unchanged. Original scope retained for audit.

STOP. No next task assigned. Do not start automatic Watchdog, Service, actual Recovery or another phase.

## Purpose
Run ONE consolidated verification gate for the complete passive monitoring-foundation bundle. This replaces per-class Work runs to conserve Work quota.

## Start point
Passive Supervisor was previously verified at65/65. Main now contains the complete passive foundation:
GuardianHeartbeat, GuardianHeartbeatRegistry, GuardianLivenessPolicy, GuardianLivenessSnapshot, GuardianMonitorConfig, GuardianEvent/GuardianEventJournal and GuardianMonitorStep, plus GuardianMonitoringFoundationTest.

## Required verification — one run
- Build/Lint.
- Run the new focused monitoring tests first. Fix only proven defects.
- Then run the existing full API25/1024x600 regression ONCE, including all prior65 tests and6 quick-action navigation returns.
- Verify exact heartbeat ordering/regression rejection, LIVE/LATE/STALE boundaries, UNKNOWN behavior, config sanitization, journal capacity/overwrite/order/retained snapshots/latest/clear, meaningful-transition-only journaling, repeated no-op monitor cycles, and complete Heartbeat -> Liveness -> Health -> Supervisor -> Recovery Recommendation composition.
- Add only missing focused tests needed to prove those behaviors; avoid combinatorial repetition already proven by earlier Guardian policy/Supervisor gates unless a changed path requires it.
- Check concurrency only where this bundle introduced synchronized mutable state: heartbeat registry and event journal/liveness capture. Use a bounded stress test, not repeated giant suites.
- Confirm final P2 Home/Apps/restart behavior remains unchanged and no autoplay.
- Confirm production adds no Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/scheduler/polling/automatic listener/disk/network/sensor/restart/recovery execution.
- Record APK/PSS/launch observations and crash/ANR status.
- Update01_CURRENT_STATUS.md, TEST_RESULTS.md, CHANGELOG.md; Commit+Push; STOP.

## Efficiency rule
Do not rerun the same full suite repeatedly when it passes. Focused test -> fix if proven -> focused retest -> ONE final full regression. Reuse prior evidence for unchanged 1024-combination/6144-policy proofs instead of regenerating them.

## Constraints
Passive foundation only. Do not start automatic watchdog/service, actual recovery, persistence/report export, source adapters, OsmAnd/GPS/Trip, Media, Vehicle, firmware/root/MCU/70mai or T3 work.

## Finish definition
Monitoring foundation is verified side-effect-free on API25/1024x600, prior behavior remains intact, evidence is checkpointed, then STOP.
