# Next Task — P4 Trip Persistence + Navigation Lifecycle

## Goal
Move from in-memory trip staging to a power-safe persistent trip foundation without coupling it to OsmAnd UI, then prepare the minimum navigation lifecycle needed when OsmAnd leaves Darbak's Activity.

## Verified incoming checkpoint
- P3 CLOSED.
- P4 Position foundation PASS.
- Live GPS -> final Home speed PASS at `ad6fa264...`, run `36672759036`.
- Lightweight OsmAnd bridge + AIDL-service capability probe PASS at `cf0d35b...`, run `36673514468`; focused suite 6/6 PASS.

## Implement now
1. Add a compact append/chunk trip format with explicit session identity and monotonically ordered points.
2. Add a storage locator that prefers a writable removable/app-external location and has a safe fallback; never overwrite an existing trip.
3. Add a writer with temp/commit or equivalent safe-close semantics so an interrupted write cannot masquerade as a complete chunk.
4. Keep `TripRecorderBuffer` independent of storage and OsmAnd.
5. Add focused tests for serialization/order, duplicate-safe names, partial/final distinction and storage fallback logic where API25 emulator permits.
6. Define a navigation lifecycle boundary so future external OsmAnd UI can coexist with Darbak trip recording; do not start an always-running Service until this persistence contract is proven.
7. Build/Lint + focused API25 tests only; fix proven defects only.

## Constraints
- No full OsmAnd SDK/AIDL vendoring.
- No route/search/favorites UI yet.
- No destructive storage cleanup; never delete user trip history in this batch.
- No full regression/Guardian exhaustive suites.
- No firmware/root/MCU/T3 changes.

## Finish
Persistent trip chunks can be written and distinguished as complete vs interrupted, storage selection is explicit/fallback-safe, and the next Service/navigation lifecycle step has a bounded contract. Commit + Push and checkpoint.
