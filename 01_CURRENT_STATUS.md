# Current Status

State: P3 CLOSED / P4 GPS + TRIP PERSISTENCE + OSMAND BRIDGE VERIFIED
Updated: 2026-09-30.
Target: t3-p3 / sun8iw11p1 / Android 7.1 API25 / ARMv7 / ~1GB / 1024x600.

## Accepted product state
- P0/P1/P2/P3 are closed within their approved scope. P3 final regression evidence remains in `TEST_RESULTS.md` and `docs/test-evidence/p3-final-20260930/`.
- The P2 Darbak OS interface remains the accepted final-product shell: Arabic RTL, 1024x600, truthful states, no fabricated live data and no media autoplay.
- Guardian remains passive. No automatic Watchdog, actual recovery executor or persistent support export has been started.

## P4 — verified so far
### Position foundation
- `PositionFix`, `PositionState` and `TripPoint` verified on API25/1024x600.
- Checkpoint `d46c8d67abcd553bbe14143c98df2b9d10b99f75`, run `36666425924`, PositionStateTest 2/2 PASS.

### Live GPS + Home speed
- `AndroidGpsSource` uses Android `LocationManager.GPS_PROVIDER`, lifecycle-owned by MainActivity. Only `ACCESS_FINE_LOCATION` is requested.
- Real fixes are validated as `PositionFix`; Home speed displays real GPS km/h or truthful `—`/GPS status.
- Checkpoint `ad6fa264d2d51567b2d1244c8b33b4b012bf08e9`, run `36672759036`: Build/Lint PASS, focused 4/4 PASS, emulator geo-fix reached the final Home speed surface, crash/ANR clean.

### Lightweight OsmAnd bridge
- `OsmAndPackages` recognizes only verified current official package IDs `net.osmand.plus`, `net.osmand`, `net.osmand.dev`, preferring stable/full.
- `OsmAndBridge` uses PackageManager/standard intents only: unavailable/launchable detection, safe launch, explicit `geo:` location opening and safe absent fallback.
- It probes official `net.osmand.aidl.OsmandAidlService` capability without binding or vendoring OsmAnd GPL AIDL/source.
- Upstream pinned in `REFERENCES.md` at `osmandapp/OsmAnd@26e32fb929b18cc6f6614855f184f5627a1fc7af`; upstream minSdk24, but physical T3/ARMv7/performance still unverified.
- Checkpoint `cf0d35b833070a6ccfb1703fea836ed8f3f11a5d`, run `36673514468`: Build/Lint PASS, focused suite 6/6 PASS, existing GPS->Home path PASS, absent fallback safe, no crash/ANR.

### Power-safe Trip persistence
- `TripRecorderBuffer` now supports write-first/commit-second persistence so points are discarded only after a successful chunk commit; points appended after a snapshot are retained.
- `TripChunkWriter` writes a versioned binary `.dtrip.part`, flushes and fsyncs, then commits by rename to `.dtrip`. Complete names are duplicate-safe and process-wide serialized so an existing trip is never intentionally overwritten.
- `TripChunkReader` accepts only committed `.dtrip` files and strictly validates magic/version/order/end marker/truncation/trailing bytes; partial files are never treated as history.
- `TripStorageLocator` prefers writable removable app-external storage, then writable app-external, then internal app storage. No broad storage permission is required.
- `TripRecorderStore` persists a frozen buffer prefix before committing that prefix in memory.
- `PositionRuntimePolicy` defines future ownership: NONE when idle/background, ACTIVITY while Darbak UI alone needs GPS, CONTINUOUS while trip recording or external navigation requires continuity. It starts no Service by itself.
- Final persistence checkpoint `17482756df30cccd453f8767e5ed2908db742067`, run `36674276957`: Source checks PASS, Build/Lint PASS, focused API25 suite 9/9 PASS, real LocationManager geo-fix still reaches Home, OsmAnd-absent fallback still safe, trip round-trip/partial rejection/duplicate naming/storage preference/runtime policy PASS, no crash/ANR.
- Artifact `11079133169`, digest `sha256:f712120d4a6ed644db34d7b6dbdc9fcad9cee67911cdbe35e2266e1a818768f8`.
- No full regression or Guardian exhaustive suites were run for these focused P4 gates.

## Proven corrections during P4
- Advanced the old P1 source guard for the approved fine-location path while keeping Service/Receiver/native/runtime-dependency restrictions until explicitly introduced.
- Fixed one source-guard syntax error and one Java content-description compile error found by CI.
- Strengthened trip final-name commit serialization before accepting persistence.

## P4 architecture boundary
- Darbak owns Position and Trip independently from OsmAnd.
- OsmAnd remains the offline map/navigation engine through a lightweight bridge first; no full SDK/library/native code embedded.
- Persistent storage contract is now proven. The next bounded step may introduce the continuous GPS/Trip owner required when Darbak UI is not foreground, with explicit API25 lifecycle/resource tests.

## Not yet done
- Continuous automatic Trip runtime while OsmAnd/Darbak is backgrounded; automatic movement/session policy.
- OsmAnd navigation state/control callbacks, search/favorites integration and installed-version/T3 compatibility validation.
- P5 Media, P6 Vehicle, P7 Apps/Settings/Standby, P8 Update/Admin/Recovery, P9-P11 T3 commissioning/integration/Stable acceptance.
- Darbak Test Station/ARMv7 and physical T3 validation for P4.
- Golden Backup/recovery commissioning before deep T3 system integration.

## Next
Introduce one lightweight continuous Position/Trip runtime for active trip/navigation use, reuse the proven persistence contract, and prove lifecycle handoff on API25 before adding further OsmAnd navigation control.

GitHub is the project-state authority. Historical detailed test evidence is retained in `TEST_RESULTS.md` and `docs/test-evidence/`.
