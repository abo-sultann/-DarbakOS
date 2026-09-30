# Next Task — P4 Continuous GPS + Automatic Trip Runtime GATE COMPLETE / STOP

## Closure — 2026-09-30
- Verified `970160233d0b7d49a71c3a807965ee551ee3ac1f`, [run36715997759](https://github.com/abo-sultann/DarbakOS/actions/runs/36715997759), attempt1 SUCCESS.
- Build/Lint PASS:0 errors,17 warnings. Initial new-test probe10 exposed9 failures; retained evidence and fixed only the proven lifecycle/recording defects. **One consolidated focused API25/1024x600 run:26/26 PASS**, plus the bounded GPS/Home/background/provider-disabled flow. Full Regression0; Guardian suites0.
- Confirmed worker-only storage/clean close, queued-point drain, safe null storage and repeated/null-intent starts, replacement ownership, Home stop/recreation handoff, real LocationManager callback delivery to the UI, automatic movement/stop/gap policy, persistence recovery and the existing cheap OsmAnd absent boundary.
- Home and returned screenshots were reviewed and are pixel-identical; one GPS receiver remained registered in the external foreground flow; no observed app crash/ANR. Evidence and before-fix proof: `docs/test-evidence/p4-continuous-runtime-20260930/`.
- Limits: emulator/test-provider inputs; installed OsmAnd UI, real T3/ARMv7, long-drive and abrupt process/power-loss acceptance remain open. This gate does not guarantee uncommitted RAM points across abrupt loss.
- **STOP. P4 remains open. Do not start the later AIDL/navigation-state or Map UX bundle in this batch.**

## Original gate request (completed within the recorded test scope)

## Purpose
Verify the first meaningful P4 integration bundle: GPS and automatic trip recording must continue when Darbak UI leaves foreground (including when OsmAnd is opened), while Home observes the same truthful PositionStore.

## Incoming implementation
- TripRuntimeService: API25 started Service, one HandlerThread worker, owns AndroidGpsSource + TripAutoRecorder.
- AndroidGpsSource can deliver Location callbacks on an explicit worker Looper.
- MainActivity no longer owns/stops GPS in onStop; it observes PositionStore and starts the runtime only after fine-location permission.
- Trip storage remains app-owned and external/removable-preferred through existing TripStorageLocator.
- Manifest registers only the non-exported runtime Service; no new permission.
- REFERENCES pins OsmAnd, OSMTracker and Breadcrumb review. No upstream code was copied.

## Required verification
Build/Lint first. Inspect for lifecycle/threading/API25 defects, duplicate GPS ownership, UI-thread violations, unsafe null storage, service restart behavior and trip persistence errors. Fix only proven defects.

Run one consolidated focused API25 gate covering:
1. existing PositionState + GPS/Home speed path;
2. TripRecorder + TripAutoRecorder + persistence/storage tests already present;
3. new runtime lifecycle/handoff tests needed to prove Home stop does not stop the Service-owned GPS and PositionStore callbacks reach UI safely;
4. OsmAnd intent bridge only as a non-regression boundary if its existing focused test is already cheap.

Do not run P3 Guardian exhaustive suites or full historical regression. Reuse prior evidence for unchanged P1-P3/P2 UI.

## Product invariants
- Real GPS only; never fabricate speed/location.
- Automatic trip recording independent of OsmAnd UI/navigation.
- Leaving Darbak/Home for OsmAnd must not stop trip recording.
- Disk writes must not run on Darbak UI thread.
- GPS gaps remain segmented/no fake bridge.
- No network dependency, fused location, Play Services, Room/Compose/MapLibre, or imported trip app.
- API25/ARMv7/~1GB/1024x600 remains the target.
- No T3/firmware/MCU work.

## Finish
Record durable evidence, update status/results, Commit + Push, STOP. P4 remains open; next bundle after this gate is the narrow OsmAnd AIDL/navigation-state integration and final Map UX.
