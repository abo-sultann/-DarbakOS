# Current Status

State: P3 CLOSED / P4 CONTINUOUS GPS + AUTOMATIC TRIP RUNTIME FOCUSED-VERIFIED / STOP
Updated: 2026-09-30.
Target: t3-p3 / sun8iw11p1 / Android 7.1 API25 / ARMv7 / ~1GB / 1024x600.

## Accepted product state
- P0/P1/P2/P3 are closed within their approved scope. P3 final regression evidence remains in `TEST_RESULTS.md` and `docs/test-evidence/p3-final-20260930/`.
- The P2 Darbak OS interface remains the accepted final-product shell: Arabic RTL, 1024x600, truthful states, no fabricated live data and no media autoplay.
- Guardian remains passive. No automatic Watchdog, actual recovery executor or persistent support export has been started.

## P4 — verified so far
### Current gate: Continuous GPS + Automatic Trip Runtime — complete / STOP
- Task base `7445bbca3a8e2fb5c3814c725e2304f1cc58a1fc`; verified code `970160233d0b7d49a71c3a807965ee551ee3ac1f`, [run36715997759](https://github.com/abo-sultann/DarbakOS/actions/runs/36715997759), attempt1 SUCCESS.
- Build/Lint first and after corrections: PASS,0 errors/17 warnings. Initial new-test probe10 tests exposed9 failures; all were corrected. **One consolidated API25/x86,1024x600/160dpi/1GB gate:26/26 PASS.** No full historical regression or Guardian suite ran.
- TripRuntimeService owns GPS and automatic trip recording independently from Home. Storage discovery, recording and clean-close writes run on its worker. Accepted queued input drains before close; repeated/null-intent starts remain safe; a retiring worker cannot clear its replacement's current position.
- MainActivity observes PositionStore on the UI thread and preserves it across fresh Activity creation. Actual LocationManager callbacks continued while Home was stopped, returned safely to Home and produced a committed background trip point. External Settings handoff retained the same process and single GPS receiver. Disabling GPS displayed truthful unavailable/— state.
- Automatic recording now rejects noncredible/out-of-order input before it changes session state, separates long gaps into distinct persisted sessions, expires pending movement across gaps and retries a failed full chunk before accepting the next point. Existing write-first buffer/chunk format was reused.
- Only three production files were corrected: TripRuntimeService, TripAutoRecorder and MainActivity. No extra permission, Service, runtime dependency, network code, native library, Guardian change or layout change. The source guard permits only this approved private Service and worker.
- APK53,219 bytes (+804 versus the incoming runtime, +2,364 versus Trip Foundation). One observation: PSS9,534KB, launch363ms, Views49/Activity1; no observed app crash/ANR. Home/live and returned screenshots were reviewed and are pixel-identical. Evidence: `docs/test-evidence/p4-continuous-runtime-20260930/`.
- Scope limits: test fixtures/emulator GPS, not physical satellites/T3; Android Settings handoff, not an installed OsmAnd version. Null-intent/replacement/orderly shutdown were tested; forced process death, abrupt power loss and long-drive behavior were not. Uncommitted RAM points are not guaranteed across abrupt loss. P4 remains open; STOP.

### Trip Recording Foundation — earlier focused gate
- Task base `559e51aa9ab9530b12c0dd40b9c4e6da31c49a1e`; tested `7430a45e9049d949a4855c7969cf0a7bc97b2042`, run `36708898480`, attempt1 SUCCESS.
- Fresh Build/Lint PASS (0 errors,17 warnings). Only `PositionStateTest`2/2 + `TripRecorderTest`6/6 ran on API25/x86,1024x600/160dpi/1GB: **8/8 PASS**. No regression, Guardian, GPS, OsmAnd, persistence or UI smoke invocation.
- `PositionQualityPolicy` and explicit in-memory `TripRecorder` are focused-verified for quality/freshness boundaries, start/pause/resume/finish/reset, gap segmentation, monotonic ordering, sequence continuity and retained immutable snapshots.
- One proven defect fixed: older/equal timestamps were accepted after pause/resume or finish/start because the new segment was empty. TripRecorder now retains the last accepted time across segments and clears it on reset. Four before/after reproduction cases and three additional focused test methods are retained; all supplied assertions remain.
- APK50,855 bytes; both crash buffers empty; no app ANR observed. No new resource benchmark, screenshot or physical T3/ARMv7 acceptance. Evidence: `docs/test-evidence/p4-trip-recording-20260930/`.
- Scope reconciliation: the task's repository-wide no-GPS/permission/disk/OsmAnd wording was already outdated at its base. Existing fine-location permission, GPS path, persistence and intent-only bridge were preserved and not exercised. This bundle adds no permission/provider/Service/thread/timer/Handler/Executor/disk/network/OsmAnd dependency/background work and has no TripRecorder caller outside the bundle/tests.
- Earlier P4 results below remain tied to their named checkpoints. Timestamp changes, `TripAutoRecorder` and `PositionStore` already present before this bundle were compiled, but their runtime behavior is not newly verified here. P4 remains open; no subsequent implementation task is authorized by this closure.

### Position foundation
- `PositionFix`, `PositionState` and `TripPoint` verified on API25/1024x600.
- Checkpoint `d46c8d67abcd553bbe14143c98df2b9d10b99f75`, run `36666425924`, PositionStateTest 2/2 PASS.

### Live GPS + Home speed
- `AndroidGpsSource` uses Android `LocationManager.GPS_PROVIDER`; its current owner is TripRuntimeService and MainActivity observes PositionStore. Only `ACCESS_FINE_LOCATION` is requested. The earlier Activity-owned checkpoint below remains historical.
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
- Continuous runtime: moved lifecycle storage/close off UI, drained queued fixes before shutdown, invalidated stopped position, protected replacement ownership and preserved the process store across Activity creation.
- Automatic recorder: rejected invalid/order-breaking inputs before state transitions, split persisted histories/pending movement across long GPS gaps, and recovered full buffers after storage failure without dropping the next accepted point.
- Fixed the in-memory TripRecorder accepting older/equal timestamps across new segment boundaries; retained focused reproduction and API25 proof.
- Advanced the old P1 source guard for the approved fine-location path while keeping Service/Receiver/native/runtime-dependency restrictions until explicitly introduced.
- Fixed one source-guard syntax error and one Java content-description compile error found by CI.
- Strengthened trip final-name commit serialization before accepting persistence.

## P4 architecture boundary
- Darbak owns Position and Trip independently from OsmAnd.
- OsmAnd remains the offline map/navigation engine through a lightweight bridge first; no full SDK/library/native code embedded.
- Continuous GPS/Trip ownership is now focused-verified on API25 with one private Service and one worker per runtime instance, using the existing app-owned persistence contract. MainActivity does not own or stop GPS.

## Not yet done
- Long-drive, actual system process-kill/power-loss behavior and installed OsmAnd/T3 background coexistence validation beyond this bounded emulator gate.
- OsmAnd navigation state/control callbacks, search/favorites integration and installed-version/T3 compatibility validation.
- P5 Media, P6 Vehicle, P7 Apps/Settings/Standby, P8 Update/Admin/Recovery, P9-P11 T3 commissioning/integration/Stable acceptance.
- Darbak Test Station/ARMv7 and physical T3 validation for P4.
- Golden Backup/recovery commissioning before deep T3 system integration.

## Next
STOP. The Continuous GPS + Automatic Trip Runtime gate is complete; P4 remains open. The incoming task identifies narrow OsmAnd AIDL/navigation-state integration and final Map UX as later work. Neither has been started by this verification batch.

GitHub is the project-state authority. Historical detailed test evidence is retained in `TEST_RESULTS.md` and `docs/test-evidence/`.
