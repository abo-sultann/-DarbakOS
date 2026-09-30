# Next Task — P4 Continuous GPS + Automatic Trip Runtime CONSOLIDATED GATE

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
