# Next Task — P4 Position Foundation FOCUSED CHECK COMPLETE / STOP

## Closure — 2026-09-30
Verified `d46c8d67abcd553bbe14143c98df2b9d10b99f75`, run36666425924 attempt1: Build/Lint PASS (0 errors,17 unchanged warnings); PositionStateTest only2/2 PASS in0.004s on API25/1024x600. No proven application defect or production/test correction needed.

Regression0, Guardian suites0, UI smoke0. No Location provider/permission, Service, thread/timer, disk/network, OsmAnd dependency or background work added. Evidence: `docs/test-evidence/p4-position-20260930/`.

STOP. This focused foundation check is complete; P4 is not phase-closed. No next task assigned and no GPS/OsmAnd/Trip integration started. Original scope retained below.

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
