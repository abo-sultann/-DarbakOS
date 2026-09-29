# Next Task — P3 Guardian monitoring foundation READY FOR WORK

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
