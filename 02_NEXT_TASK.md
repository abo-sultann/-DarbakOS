# Next Task — P3 Guardian Diagnostics Record READY FOR FOCUSED CHECK

## Purpose
Verify the small passive GuardianDiagnosticRecord bundle added after the closed Monitor Session gate.

## Added scope
- Immutable compact diagnostic record derived from a frozen GuardianMonitorSession Snapshot.
- Overall health and per-health component counts.
- Event count and latest component/health/monotonic transition time.
- Conservative level-0 recovery recommendation only; no recovery execution.
- Focused API25 tests for null/UNKNOWN and retained frozen-session behavior.

## Required check
1. Build/Lint.
2. Run GuardianDiagnosticRecordTest only first.
3. Fix only proven defects and rerun only the affected focused test.
4. Confirm no Service/Receiver/permission/dependency/native/background/I/O/recovery execution was added.
5. Do not run the old 1024/6144 giant suites and do not run a full regression yet.
6. Commit + Push evidence if changes are needed, then STOP.

## Constraints
Do not start Watchdog, automatic emitters, persistence/export, Health Center UI, OsmAnd/Trip, Media, Vehicle, hardware or T3 work.

## Finish definition
The new diagnostic value compiles and its focused API25 behavior is proven. This is not a phase-closing regression gate.
