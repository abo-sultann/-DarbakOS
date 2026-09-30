# Current Status

State: P3 CLOSED / P4 GPS + TRIP STAGING + OSMAND BRIDGE VERIFIED
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
- Real fixes are converted to validated `PositionFix`; Home speed displays real GPS km/h when available and otherwise keeps truthful `—`/GPS status text.
- `TripRecorderBuffer` provides a bounded ordered in-memory chunk buffer with immutable snapshot/drain semantics. No disk persistence yet.
- Verified code: `ad6fa264d2d51567b2d1244c8b33b4b012bf08e9`, run `36672759036`: Build/Lint PASS, focused 4/4 PASS, emulator LocationManager geo-fix reached the final Home speed surface; crash/ANR checks clean.

### Lightweight OsmAnd bridge
- `OsmAndPackages` recognizes only current official package IDs verified upstream: `net.osmand.plus`, `net.osmand`, `net.osmand.dev`; stable/full is preferred over free/nightly when more than one is present.
- `OsmAndBridge` uses PackageManager/standard Android intents only. It can report unavailable/launchable, launch a resolved OsmAnd package, open a validated location through an explicit `geo:` intent and fail safely when OsmAnd is absent.
- Bridge also probes the official exported `net.osmand.aidl.OsmandAidlService` capability without binding or vendoring OsmAnd AIDL/GPL source.
- Upstream reference pinned in `REFERENCES.md` at `osmandapp/OsmAnd@26e32fb929b18cc6f6614855f184f5627a1fc7af`; current upstream minSdk is 24, so API25 is not excluded by manifest level, but physical T3/ARMv7/performance remains unverified.
- Verified code/test checkpoint: `cf0d35b833070a6ccfb1703fea836ed8f3f11a5d`, run `36673514468`: Build/Lint PASS, focused API25 suite 6/6 PASS, existing real GPS -> Home path still PASS, OsmAnd-absent fallback safe, no crash/ANR.
- Artifact `11078368483`, digest `sha256:509351607a1d9088f26e44d47f0a04d831f64e35d1ef19269f74ccdbd1dd7f29`.
- No full regression or Guardian exhaustive suite was run for these focused P4 gates.

## Proven corrections during P4
- Advanced the old P1 source guard so the approved fine-location GPS path is permitted while Service/Receiver/native/runtime dependency restrictions remain enforced.
- Fixed one source-guard syntax error found by CI.
- Fixed one Java compile error (`setContentDescription(int)` -> resolved String) found by CI.
- No production defect was found in the OsmAnd bridge focused gate.

## P4 architecture boundary
- Darbak owns Position state and Trip recording independently of OsmAnd.
- OsmAnd remains the primary offline map/navigation engine; Darbak starts with a lightweight capability/intent bridge and adds AIDL only where a tested integration benefit justifies it.
- No OsmAnd SDK/library/native code has been embedded.
- No background GPS Service has been added; GPS currently follows MainActivity lifecycle. This is deliberate until persistent Trip/Navigation lifecycle is introduced.

## Not yet done
- Persistent automatic Trip Recorder/chunk files and recovery across power loss.
- OsmAnd navigation state/control callbacks, search/favorites integration and installed-version/T3 compatibility validation.
- P5 Media, P6 Vehicle, P7 Apps/Settings/Standby, P8 Update/Admin/Recovery, P9-P11 T3 commissioning/integration/Stable acceptance.
- Darbak Test Station/ARMv7 and physical T3 validation for P4.
- Golden Backup/recovery commissioning before deep T3 system integration.

## Next
Implement power-safe Trip persistence/lifecycle foundation while keeping Position/Trip independent from OsmAnd, then add the smallest navigation-control slice that can be validated without importing the full OsmAnd SDK.

GitHub is the project-state authority. Historical detailed test evidence is retained in `TEST_RESULTS.md` and `docs/test-evidence/`.
