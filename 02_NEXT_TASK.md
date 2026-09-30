# Next Task — P4 Trip Recording Foundation FOCUSED CHECK COMPLETE / STOP

## Closure — 2026-09-30
- Tested `7430a45e9049d949a4855c7969cf0a7bc97b2042`, [run36708898480](https://github.com/abo-sultann/DarbakOS/actions/runs/36708898480), attempt1 SUCCESS.
- Fresh Build/Lint PASS:0 errors,17 warnings. Exactly one focused API25/1024x600 invocation: `PositionStateTest`2/2 + `TripRecorderTest`6/6, **8/8 PASS**. No Full Regression, Guardian suites, GPS/OsmAnd/persistence tests or UI smoke.
- Fixed only the proven monotonic-order defect across pause/resume and finish/start segments. Before/after four-case reproduction, strengthened focused tests, raw Android results, lint, crash logs and provenance are saved in `docs/test-evidence/p4-trip-recording-20260930/`.
- The bundle remains explicit in-memory input only, without a new provider/permission/Service/thread/timer/Handler/Executor/I/O/dependency/background path. The repository already contained GPS, fine-location permission, persistence and an intent-only OsmAnd bridge before this task; those existing paths were not removed, changed or exercised. Accordingly, the historical absence wording below is confirmed for additions in this bundle, not claimed for the whole repository.
- P4 remains open. This closes only the requested foundation check, not automatic recording, persistence integration, live GPS or OsmAnd acceptance.
- **STOP. No next implementation task or phase is assigned.**

## Original gate request (completed within the scope clarified above)

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
