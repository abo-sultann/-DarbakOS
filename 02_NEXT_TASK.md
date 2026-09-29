# Next Task — P3 Guardian Monitor Session READY FOR WORK

## Purpose
Run ONE small consolidated verification gate for the passive GuardianMonitorSession bundle added after the verified Monitoring Foundation.

## Scope added since verified checkpoint
- GuardianMonitorSession owns HeartbeatRegistry + EventJournal + MonitorConfig + explicit caller-driven evaluate.
- Cold-boot generation isolation.
- Delayed old-generation heartbeat rejection.
- Immutable diagnostics Snapshot containing generation, frozen Guardian health, retained per-component latest heartbeat and event history.
- GuardianMonitorSessionTest focused coverage.

## Required verification — one run
1. Build/Lint.
2. Run GuardianMonitorSessionTest first; fix only proven defects.
3. Run the existing monitoring-foundation focused tests plus a bounded regression sufficient to prove no P2/Core/Guardian behavior changed. Reuse unchanged giant-proof evidence; do not regenerate 1024/6144 combination suites.
4. Verify:
   - old-generation heartbeat rejection after cold boot;
   - current-generation heartbeat acceptance;
   - reset clears current heartbeats/journal/health and returns UNKNOWN;
   - retained prior Result/Snapshot remains immutable;
   - diagnostics snapshot freezes generation + health + each component heartbeat + events;
   - null/empty behavior is safe;
   - generation saturation at Long.MAX_VALUE cannot overflow;
   - no automatic evaluation/recovery.
5. Confirm production adds no Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/scheduler/polling/listener/disk/network/sensor/restart/recovery execution.
6. Record evidence and update 01_CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md; Commit+Push; STOP.

## Efficiency
Focused tests -> fix proven defect if any -> focused retest -> ONE bounded regression. Do not repeat already-proven giant Guardian suites.

## Constraints
Passive session only. Do not start Watchdog/Service/automatic heartbeat emitters, actual recovery, persistence/report export, source adapters, OsmAnd/GPS/Trip, Media, Vehicle, firmware/root/MCU/70mai or T3 work.

## Finish definition
GuardianMonitorSession is verified side-effect-free on API25/1024x600 and prior final UI/Core/Guardian behavior remains intact; evidence checkpointed; STOP.
