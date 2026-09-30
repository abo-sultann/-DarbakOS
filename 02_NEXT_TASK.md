# Next Task — P4 Final Map + OsmAnd External API CONSOLIDATED GATE

## Purpose
Close the current P4 Map/navigation bundle without widening scope. Verify the final Darbak Map control surface built on OsmAnd's documented external API while preserving the already verified continuous GPS + automatic trip runtime.

## Incoming code boundary
Base after the continuous-runtime closure: `4f08c429d61fcaec4c0a1d0e3135eaa8c1c5fb5c`.
Current candidate: `40a73edba292ad11296291e198c8c02309b7e169`.

Implemented since base:
- truthful `OsmAndNavigationSnapshot` with UNKNOWN/IDLE/ACTIVE + freshness;
- OsmAnd external `osmand.api` intents for get_info/show_location/navigate_search with safe geo fallback;
- final Arabic/RTL Map surface: search, current location, open OsmAnd, route refresh and route summary;
- Home navigation card consumes only fresh returned OsmAnd route data;
- missing/limited OsmAnd remains explicit and truthful;
- capability caching/refresh avoids repeated PackageManager work;
- dedicated Map back action and focused physical smoke support;
- no OsmAnd source vendored and no new runtime dependency.

## Verification
Run Build/Lint, then one bounded API25/x86 1024x600/160dpi/~1GB gate for this bundle. Reuse prior P1-P3 and continuous-runtime evidence; do not run Full Regression or Guardian suites.

Must cover:
1. OsmAnd package preference and absent/limited/full external-API boundaries.
2. `get_info` parsing: no destination => IDLE; valid destination => ACTIVE; invalid/non-finite/out-of-range destination => UNKNOWN; freshness expiry.
3. Search/current-location/open intents are package-scoped and fail safely when OsmAnd/API is absent.
4. Final Map RTL/fit/touch at 1024x600; search action and dedicated back-to-Home.
5. Home navigation remains truthful for missing/limited/unknown/idle/active states; no fabricated ETA/distance/turn.
6. Continuous TripRuntimeService remains alive while Darbak hands foreground to an external activity; no duplicate GPS owner and no trip-runtime regression.
7. No media autoplay, new permission, network dependency, native library, OsmAnd source copy or heavy map framework.

Inspect and fix only proven defects. Do not add AIDL binding merely because the service exists: the external API is the chosen lightweight boundary for this P4 bundle unless a verified missing requirement forces AIDL.

## Finish
Write durable evidence under `docs/test-evidence/p4-map-osmand-20260930/`, update `TEST_RESULTS.md`, `01_CURRENT_STATUS.md` and this file, Commit + Push, then STOP.

If all requested behavior passes, mark **P4 CLOSED within emulator/API25 scope**. Explicitly leave installed real OsmAnd version, physical T3/ARMv7 GPS, long-drive and sudden-power-loss acceptance for the later hardware commissioning/integration gates; do not block P4 emulator closure on hardware that is intentionally unavailable here.
