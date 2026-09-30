# P4 Final Map + OsmAnd External API — closure evidence

Date: 2026-09-30
Candidate: `40a73edba292ad11296291e198c8c02309b7e169`
Gate task: `7f025394691a96ee9e593cd2438dcb2ef127eb70`
GitHub Actions run: `36751414971`, job `110010717261`, conclusion `SUCCESS`.

## Verified boundary
- API25 / x86 emulator / 1024x600 / 160dpi / 1GB RAM.
- Source guard PASS: fine-location only, one private TripRuntimeService/worker, no extra Receiver/runtime dependency/native code.
- Build + AndroidTest APK + Lint PASS.
- One bounded `--map-gate`; no Full Regression and no Guardian suite.
- Focused selection is 35 tests: PositionState, GPS/Trip, TripRecorder, TripPersistence, OsmAndBridge, TripAutoRecorder, TripRuntime and Shell.
- The runner asserts `OK (35 tests)` before continuing; the successful job therefore proves the complete focused selection passed.
- Real emulator `GPS_PROVIDER` geo-fix reached Home; Media remained stopped.
- With no OsmAnd installed, Home and Map stayed truthful: navigation unavailable, OsmAnd unavailable, and Map actions that require the engine disabled.
- Final Map physical surface at 1024x600 was captured; dedicated Map-back listener is covered by ShellTest and bottom navigation physical smoke returned to Home.
- TripRuntimeService remained started while an external Activity (Android Settings) held foreground; the same process continued and returned to Home with live GPS.
- GPS provider disable returned the final truthful `—` / unavailable state.
- Crash buffer contained no FATAL EXCEPTION and no app ANR was observed.
- Uploaded artifact: `11114692930`, ZIP digest `0a796169d6130686741617cd603b73f7efae24d65f22dee50a5f2653ccab6c8a`.

## OsmAnd contract proof in the selected tests
`OsmAndBridgeTest` verifies stable package preference, safe absent-engine behavior, documented `get_info` idle state, active route field parsing, route freshness expiry, and rejection of invalid destination coordinates. Production integration uses package-scoped `osmand.api` intents with a safe `geo:` fallback and does not vendor OsmAnd GPL source/AIDL classes.

## Scope limits retained for later hardware/integration gates
- No real installed OsmAnd APK/version was exercised by CI; installed-version compatibility remains a T3/Test Station integration check.
- No physical T3/ARMv7 satellite GPS, long-drive, forced process-death, or sudden-power-loss acceptance is claimed.
- A transient `uiautomator` console message (`could not get idle state`) appeared during evidence capture, but the bounded runner continued and all explicit assertions, screenshots, focused instrumentation, crash/ANR guards and job conclusion passed; it is not recorded as an application defect.

## Closure decision
P4 is closed within its emulator/API25 software scope. Position, continuous automatic Trip recording/persistence, final Map control surface and the lightweight OsmAnd external-API boundary are implemented and bounded-verified. Hardware/installed-OsmAnd acceptance remains intentionally deferred to the later commissioning/integration gates.
