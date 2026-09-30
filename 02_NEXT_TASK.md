# Next Task — P4 Trip Recording Foundation READY FOR FOCUSED CHECK

## Purpose
Verify the bundled P4 position-quality + in-memory Trip Recorder foundation. Do not start Android GPS provider or OsmAnd yet.

## Added
- PositionQualityPolicy: shared conservative fix-quality gate; max age15s, invalid/future/stale/poor-accuracy/implausible-speed rejection.
- TripRecorder: explicit start/pause/resume/finish/reset; immutable snapshots; monotonic ordering.
- Pause/resume and long GPS gaps create separate segments so no false straight-line bridge is recorded.
- TripRecorderTest focused coverage.

## Required
1. Build/Lint.
2. Run PositionStateTest + TripRecorderTest only.
3. Fix only proven defects; rerun affected focused tests only.
4. Confirm API25 compatibility and no Location permission/provider, Service, thread/timer, Handler/Executor, disk/network, OsmAnd dependency or background work.
5. No full regression and no Guardian suites.
6. Record focused evidence, update status/history, Commit + Push, STOP.

## Constraints
Automatic Trip Recorder remains independent of OsmAnd. Offline-first. UNKNOWN/no data is preferred to fabricated position. No persistence yet; safe chunked persistence/crash recovery comes after this in-memory gate. No real GPS polling or route UI in this gate.

## Finish
Position quality and segmented in-memory trip recording are focused-verified on API25; P4 remains open.
