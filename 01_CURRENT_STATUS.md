# Current Status

State: P3 CLOSED / P4 CLOSED (API25 emulator scope) / P5 READY
Updated: 2026-09-30.
Target: t3-p3 / sun8iw11p1 / Android 7.1 API25 / ARMv7 / ~1GB / 1024x600.

## Accepted product state
- P0/P1/P2/P3 are closed within their approved scope. Detailed historical evidence remains in `TEST_RESULTS.md` and `docs/test-evidence/`.
- P2 final-product shell remains accepted: Arabic RTL, 1024x600, truthful states, no fabricated live data and no media autoplay.
- Guardian remains passive. Automatic watchdog/recovery executor/persistent support export stay deferred to later recovery/integration phases.

## P4 — CLOSED within emulator/API25 software scope
### Final Map + OsmAnd External API closure
- Software candidate `40a73edba292ad11296291e198c8c02309b7e169`; closure task `7f025394691a96ee9e593cd2438dcb2ef127eb70`.
- GitHub Actions run `36751414971`, job `110010717261`: SUCCESS.
- Source checks PASS. Build + AndroidTest APK + Lint PASS.
- One bounded API25/x86, 1024x600/160dpi/1GB `--map-gate`; focused selection **35/35 PASS** by the runner assertion. Full Regression0; Guardian suites0.
- Final Map surface is Arabic/RTL and includes destination search, current location, explicit OsmAnd open, route refresh and truthful route summary.
- `OsmAndNavigationSnapshot` exposes UNKNOWN/IDLE/ACTIVE with freshness; Home consumes only fresh returned route state and never invents turn/distance/ETA values.
- OsmAnd integration uses documented package-scoped `osmand.api` external intents with safe `geo:` fallback. No OsmAnd source/AIDL classes, SDK, native library or heavy map framework are embedded.
- In the no-OsmAnd CI environment, Home/Map explicitly show unavailable state and disable engine-dependent actions.
- `OsmAndBridgeTest` covers stable package preference, safe absent-engine behavior, `get_info` idle/active parsing, freshness expiry and invalid destination rejection.
- Continuous TripRuntimeService remained alive across an external foreground Activity; real emulator GPS_PROVIDER fixes returned to Home, Media stayed stopped, provider disable returned `—`/unavailable, and no app crash/ANR was observed.
- Durable closure evidence: `docs/test-evidence/p4-map-osmand-20260930/RESULT.md`.
- Artifact `11114692930`; digest `0a796169d6130686741617cd603b73f7efae24d65f22dee50a5f2653ccab6c8a`.

### P4 components accepted from earlier focused gates
- Position foundation: `PositionFix`, `PositionState`, `TripPoint`; API25 focused verified.
- Real GPS/Home speed: Android `GPS_PROVIDER`, fine-location only, truthful unavailable handling.
- Automatic trip runtime: one private Service + one HandlerThread worker; GPS/trip continuity independent of Home/OsmAnd foreground.
- Trip recording policy: credible ordered fixes, movement start/stop, gap-separated sessions, no fabricated bridges.
- Persistence: write-first/commit-second chunks, fsync + `.part` then rename, strict reader, duplicate-safe names, removable/external-preferred app storage with internal fallback.
- Proven lifecycle/storage defects found during P4 were fixed before closure; the detailed before/after evidence remains in `TEST_RESULTS.md` and `docs/test-evidence/p4-continuous-runtime-20260930/`.

## P4 architecture boundary
- Darbak owns Position and Trip independently from OsmAnd.
- OsmAnd is the offline map/navigation engine; Darbak uses a lightweight external API boundary first rather than forking/embedding the engine.
- Installed real OsmAnd version compatibility, physical T3/ARMv7 GPS, long-drive behavior, forced process death and sudden power loss are hardware/integration acceptance items, not blockers to the completed emulator/API25 software phase.

## Not yet done
- P5 Media.
- P6 Vehicle.
- P7 Apps/Settings/Standby/alerts.
- P8 Update/Admin/Recovery.
- P9 real T3 commissioning + Golden Backup/recovery verification.
- P10 T3 integration/OEM hiding/autostart/boot.
- P11 Stable acceptance.
- Darbak Test Station/ARMv7 and physical T3 acceptance for P4/P5+.

## Next
P5 Media is ready. Apply the reuse-first rule before implementation: inspect Android API25 MediaSession/MediaBrowser capabilities, compatible lightweight open-source car/media projects and the owner's prior repos; adapt only what materially shortens the work. Preserve the existing no-autoplay cold-boot rule and the T3 resource budget.

GitHub is the project-state authority.
