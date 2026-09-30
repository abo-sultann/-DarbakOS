# Next Task — P4 OsmAnd Bridge Foundation

## Goal
Add the smallest safe OsmAnd integration boundary for Darbak OS without embedding the full OsmAnd SDK and without coupling Position/Trip to OsmAnd.

## Verified incoming checkpoint
- P3 CLOSED.
- P4 Position foundation PASS.
- P4 live GPS + Home speed + TripRecorderBuffer focused gate PASS at `ad6fa264d2d51567b2d1244c8b33b4b012bf08e9`, run `36672759036`.
- API25/1024x600 focused tests 4/4 PASS; emulator LocationManager geo-fix reached the final Home speed surface.

## Implement in this batch
1. Add a small `OsmAndBridge`/capability abstraction using Android PackageManager/Intent capabilities only first.
2. Detect known installed OsmAnd package variants conservatively; absent/unsupported remains a truthful unavailable state.
3. Provide safe explicit open/launch behavior only when a compatible package/activity resolves.
4. Keep PositionState and TripRecorderBuffer independent from OsmAnd.
5. Add focused API25 tests for absent package, supported package resolution logic and safe no-crash fallback.
6. Record the exact upstream OsmAnd API/AIDL reference used in `REFERENCES.md`.
7. Build/Lint + focused tests only. Fix proven defects only.

## Constraints
- No full OsmAnd SDK/library dependency.
- Do not vendor the full current AIDL surface in this first batch.
- No route/search/favorites/navigation callbacks yet.
- No background Service, GPS persistence, media/vehicle work or T3 changes.
- No full regression or Guardian exhaustive suites.

## Finish
A lightweight tested bridge boundary exists and Darbak can truthfully distinguish OsmAnd unavailable vs launchable without destabilizing the current P4 GPS/Home path. Commit + Push and checkpoint; then proceed to the navigation/AIDL compatibility slice.
