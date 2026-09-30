# Next Task — P4 Position Foundation READY FOR FOCUSED CHECK

## Purpose
Verify the first small P4 foundation without starting GPS/OsmAnd integration yet.

## Added
- PositionFix: immutable validated lat/lon/accuracy/speed/monotonic-time value and km/h conversion.
- PositionState: explicit in-memory latest fix, rejects null/non-monotonic updates, cold reset.
- TripPoint: immutable accepted point value.
- PositionStateTest focused API25 coverage.

## Required
Build/Lint, then PositionStateTest only. Fix only proven defects and rerun affected focused test. Confirm no Location permission/provider, Service, thread/timer, disk/network, OsmAnd dependency or background work was added. Do not run full regression or old Guardian exhaustive suites. Record focused evidence, Commit + Push, STOP.

## Forbidden
No real GPS polling, OsmAnd/AIDL, trip persistence, route UI, Media/Vehicle, hardware/T3 work in this gate.

## Finish
P4 position value/state foundation compiles and passes focused API25 tests. Not a phase-closing gate.
