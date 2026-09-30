# Current Status

State: P3 CLOSED / P4 GPS + TRIP STAGING VERIFIED / OSMAND BRIDGE NEXT
Updated: 2026-09-30.
Target: t3-p3 / sun8iw11p1 / Android 7.1 API25 / ARMv7 / ~1GB / 1024x600.

## Accepted product state
- P0/P1/P2/P3 are closed within their approved scope. P3 final regression evidence remains in `TEST_RESULTS.md` and `docs/test-evidence/p3-final-20260930/`.
- The P2 Darbak OS interface remains the accepted final-product shell: Arabic RTL, 1024x600, truthful states, no fabricated live data and no media autoplay.
- Guardian remains passive. No automatic Watchdog, actual recovery executor or persistent support export has been started.

## P4 — verified so far
### Position foundation
- `PositionFix`, `PositionState` and `TripPoint` verified on API25/1024x600.
- Focused checkpoint: `d46c8d67abcd553bbe14143c98df2b9d10b99f75`, run `36666425924`, PositionStateTest 2/2 PASS.

### Live GPS + Home speed + trip staging
- `AndroidGpsSource` uses Android `LocationManager.GPS_PROVIDER`, lifecycle-owned by MainActivity. Only `ACCESS_FINE_LOCATION` is requested.
- Real fixes are converted to validated `PositionFix`; Home speed now displays real GPS km/h when available and otherwise keeps truthful `—`/GPS status text.
- `TripRecorderBuffer` provides a bounded ordered in-memory chunk buffer with immutable snapshot/drain semantics. No disk persistence yet.
- Focused tests cover Position state, Android Location -> PositionFix conversion and trip-buffer bounds/order/reset.
- Verified code: `ad6fa264d2d51567b2d1244c8b33b4b012bf08e9`.
- Successful run: `36672759036` on API25/x86, 1024x600, 1GB.
- Build/Lint PASS. Focused instrumentation 4/4 PASS. Emulator `geo fix` traveled through the real LocationManager path to the final Home speed surface; `GPS • مباشر` and a numeric speed were observed.
- Existing navigation remains `لا يوجد مسار نشط`; media remains `متوقف`; crash buffer/ANR check clean in the focused run.
- Artifact: `11078124989`, digest `sha256:1cb4870df55cfd62e8a8b65f619ca69aa45f8e1fdd3c3fded92a1cef807faffb`.
- No full regression or Guardian exhaustive suites were run for this focused gate.

## Proven corrections during this P4 bundle
- Advanced the old P1 source guard so the approved fine-location GPS path is permitted while Service/Receiver/native/runtime dependency restrictions remain enforced.
- Fixed one source-guard syntax error found by CI.
- Fixed one Java compile error (`setContentDescription(int)` -> resolved String) found by CI.
- No other production defect was observed in the successful focused gate.

## P4 architecture boundary
- Darbak owns Position state and Trip recording independently of OsmAnd.
- OsmAnd remains the primary offline map/navigation engine and should be integrated through a small bridge/AIDL or compatible intent surface before considering any heavy SDK embedding.
- Trip persistence, route state, search/favorites and OsmAnd navigation callbacks are not implemented yet.
- No background GPS Service has been added; GPS currently follows MainActivity lifecycle. This is deliberate until the Trip/Navigation lifecycle boundary is designed and tested.

## Not yet done
- OsmAnd capability detection/bridge, map launch/control, navigation state/callbacks, search/favorites integration.
- Persistent automatic Trip Recorder/chunk files and recovery across power loss.
- P5 Media, P6 Vehicle, P7 Apps/Settings/Standby, P8 Update/Admin/Recovery, P9-P11 T3 commissioning/integration/Stable acceptance.
- Darbak Test Station/ARMv7 and physical T3 validation for the new P4 GPS path.
- Golden Backup/recovery commissioning before deep T3 system integration.

## Next
Build the smallest API25-compatible OsmAnd bridge foundation first: installed/capability detection, safe launch/open-map fallback and an interface boundary that does not couple Position/Trip to OsmAnd. Verify focused behavior before adding navigation/search callbacks.

GitHub is the project-state authority. Historical detailed test evidence is retained in `TEST_RESULTS.md` and `docs/test-evidence/`.
