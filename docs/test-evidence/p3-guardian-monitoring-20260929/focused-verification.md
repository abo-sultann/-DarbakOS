# Monitoring foundation focused verification

Incoming gate: `f8ed0818f5597dc067421d3c1c2324a2a86c975b`.

## Proven defects and limited fixes

1. `compile-before.txt`: compiling the production core fails at four calls to nonexistent `GuardianSnapshot.state(Component)` in the new MonitorStep. Fix: a private field-to-component adapter inside MonitorStep; the previously verified Snapshot API is unchanged. The new foundation tests also referred to nonexistent `aggregateHealth`; corrected to the existing `overall` field.
2. `before-fix.txt`: focused config boundary test proves `GuardianMonitorConfig(Long.MAX_VALUE, 0)` returns late=Long.MAX_VALUE and stale=0 due to `late + 1` overflow. Fix: cap late at Long.MAX_VALUE-1, reserving one millisecond for a strictly later stale threshold. Negative/default/normal/equal/upper boundaries remain covered.

## Focused-first sequence

`python scripts/check_monitoring.py` compiles the real production core plus `MonitoringFocusedChecks` on JDK17 and runs ten dependency-free focused checks. The identical check bodies are invoked by ten added Android tests, alongside the six supplied foundation tests. This local preflight does not replace API25 validation.

`after-fix.txt`: all10 local checks PASS, including two bounded200-round concurrency checks for the heartbeat registry/liveness capture and event journal. Test workers are joined; no production worker is added.

The Android gate first runs only GuardianMonitoringFoundationTest (16 tests). Only after it passes, it starts one regression invocation (74 tests:58 prior +16 foundation), then the existing six actual-tap navigation returns, Home/Apps/Cold Restart, honest unavailable states/no-autoplay, screenshots and resource/crash capture.

## Owner-requested evidence reuse

The latest user instruction explicitly excludes unchanged giant proofs. This takes precedence over the older task bullet saying to rerun all65 prior tests. All65 remain in source unchanged:58 are selected for this regression;7 giant proofs reuse the successful65-test evidence at `ee62972ec8a0372f7ec682220bdc21e183b30405`, run36475157726, `docs/test-evidence/p3-guardian-supervisor-20260928/`.

The seven reused methods are recorded in `regression-selection.json` by the Android gate. They cover unchanged1024/6144-combination proofs and old20000-iteration Snapshot/Supervisor concurrency. `scripts/guardian_reused_proofs.json` pins SHA256 of all seven production Guardian primitives and the five affected prior test classes. The gate refuses reuse if any fingerprint changes. No prior test or assertion is removed or weakened, and no new giant combination suite is generated.

Reuse review: REFERENCES.md and the existing Java/platform/test conventions were reviewed. The new tests use the existing Guardian primitives and platform collections/concurrency APIs; no third-party code, runtime dependency, background work or recovery executor is introduced.
