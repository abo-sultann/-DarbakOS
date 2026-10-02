# Current Status

State: P3 CLOSED / P4 CLOSED / P5 CLOSED / P6 CLOSED (API25 emulator software scope)
Updated: 2026-10-02.
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

## P5 Media — API25 software scope CLOSED
- Incoming main `d61cfc2a0385018e4e53a6d72c018cdcdd7be7e1`; verified code `7c4bf543eb11a3742dfde7ed2c75d4e1ca69faa2`.
- [Run36818005050](https://github.com/abo-sultann/DarbakOS/actions/runs/36818005050), job110227196579, attempt1 SUCCESS. Build/Lint PASS:0 errors,17 warnings.
- Same P5 focused gate: **11/11 PASS** on API25/x86,1024x600/160dpi/1GB, in three configured invocations (9 +1 +1). Full Regression0; Guardian suites0; separate P4/OsmAnd suites0.
- Original run36817089400 stopped before any tests because two literal backslash-n separators broke Python syntax. After that correction, run36817725413 passed9 +1 tests but its parser rejected JUnit's singular `OK (1 test)`. Both proven runner defects are fixed; no production or instrumentation assertion changed.
- Real Android Settings granted notification-listener access. A separate fixture APK/UID exposed a paused framework MediaSession; Darbak observed it without autoplay, then explicit Play and Next updated the external session and Home metadata. Removing the fixture returned Media/Home to stopped idle with disabled transport.
- Existing shell navigation/recreation/RTL/fit checks passed. Both captured Home screenshots reviewed; truthful unavailable/idle media text, no observed overlap. No app crash/ANR in the captured run.
- APK71,475 bytes, delta0 versus incoming; all10 ZIP-entry payloads identical.89 production/test/fixture/build/workflow input fingerprints unchanged. P4/OsmAnd and their accepted evidence remain untouched.
- Durable evidence: `docs/test-evidence/p5-external-media-20261001/`, including both pre-fix failures, final raw results/logs/Settings UI trees, screenshots, hashes and source comparison.
- This closes only the current verification gate. P5 remains open outside this foundation; real player/version/audio output, full device boot/wake and ARMv7/Test Station/T3 acceptance are not established.

## P5 closure addendum — 2026-10-02
- Consolidated API25 gate on `7d0f40bac34e8c2043abc386acbdd4f9f5fd6b24` succeeded, including real framework local WAV playback, Play/Pause/Resume, Audio Focus, external MediaSession arbitration, persistent validated local-media manifest and no-autoplay behavior.
- Follow-up lifecycle guard `55eb48c6190b6d43b0b1904003a4040b09774b77` also passed CI.
- P5 is closed for emulator/API25 software scope. Physical ARMv7/T3 audio, removable-media vendor behavior and real third-party players remain P9/P10 acceptance items, not P5 blockers.

## P6 Vehicle — CLOSED within emulator/API25 software scope
- Unified immutable `VehicleValue` / `VehicleSnapshot` contract is in place with source provenance, observed timestamp and freshness enforcement.
- `VehicleSourceAdapter` is optional/pluggable; `VehicleDataStore` merges per-field readings and chooses the newest available observation.
- Missing, future-dated and stale values resolve to explicit unavailable state; stale data is never presented as live.
- Vehicle UI consumes only the unified snapshot contract and remains truthful with no connected source.
- Run 36978379715 on commit `1d8d44f3377aec088f1b71fa81dce952ec6bcea3`: Source checks + Build + Lint PASS; P5 compatibility gate PASS; P6 focused `VehicleDataTest` **4/4 PASS** on API25/x86, 1024x600/160dpi/1GB. Full Regression0; Guardian suites0.
- P6 integration exposed one stale P5 ShellTest assumption that Vehicle still used the generic placeholder panel; the test was corrected to the dedicated Vehicle panel without changing P5 production behavior.
- Artifact `11214612250`.
- Physical TPMS ESP32/CC1101, Toyota OBD/CAN, fridge protocols, ARMv7/Test Station and real T3 connectivity remain independent P9/P10 hardware acceptance work and did not block P6.

## P7 — OPEN; Apps first slice PASS
- Apps now discovers launchable activities through Android `CATEGORY_LAUNCHER`, excludes Darbak itself and reports a bounded truthful summary without adding a dependency, permission, Service or process.
- Recent/Favorites remain visibly disabled because their persistence/behavior is not implemented yet. Android app-management entry is the only newly enabled action.
- Run `36986279699` on `8ed626e7988997e6d253f7df0ef856fcc5e6c03e`: Source checks + Build + Lint PASS; preserved P5/P6 focused gates PASS; P7 Apps focused ShellTest **1/1 PASS** on API25. Full Regression0; Guardian suites0.
- Artifact `11217272345`.
- Next P7 slice is user Settings; Standby and actionable alerts follow separately.

## P7 Settings — first slice PASS
- Dedicated user Settings surface replaces the generic placeholder; technical Admin remains separate/deferred.
- One truthful persisted setting is implemented: show/hide the Home speed card. It changes presentation only and does not stop GPS or TripRuntime recording.
- Run `36988422227` on `eb0af063d6beb7ff4bfe471cca96c8b50d38fae2`: Source checks + Build + Lint PASS; isolated P5 + P6 + P7 Apps/Settings focused gates PASS on API25. Full Regression0; Guardian suites0. Artifact `11218641454`.
- Two runner/test assumptions were corrected during integration: navigation now recognizes the dedicated Settings panel, and P5 selects its historical Shell tests explicitly so later P7 test additions cannot change its expected count.

## Not yet done
- P7 Standby/alerts and later Apps Recent/Favorites persistence.
- P8 Update/Admin/Recovery.
- P9 real T3 commissioning + Golden Backup/recovery verification.
- P10 T3 integration/OEM hiding/autostart/boot.
- P11 Stable acceptance.
- Darbak Test Station/ARMv7 and physical T3 acceptance for P4/P5+.

## Next
Continue P7 with the bounded Standby slice. Standby must remain lightweight and must not stop Position/Trip recording; alerts remain a separate later slice.

GitHub is the project-state authority.
