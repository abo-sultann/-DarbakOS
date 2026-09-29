# Test Results

No Darbak OS release is Stable. See the per-stage evidence below.

## 2026-09-29 — P3 Guardian Monitoring Foundation consolidated gate: PASS / STOP

- Incoming `f8ed0818f5597dc067421d3c1c2324a2a86c975b`; tested `0293c4e9cef7f102cacbc99c0905f9b3c95379c7`. [Run36573126651](https://github.com/abo-sultann/DarbakOS/actions/runs/36573126651), job109421810853, attempt1 SUCCESS. One consolidated gate; no subsequent phase started.
- Focused first:10 dependency-free Java checks PASS after the proven fixes; then **16/16 API25 focused tests PASS** in0.134s. Only then **one74/74 API25 regression PASS** in4.078s, followed by **6/6 actual-tap navigation returns PASS**. Build/Lint PASS:0 errors,17 existing warnings.
- Latest owner instruction explicitly requires reusing unchanged giant proofs. Therefore this regression selects58 prior tests +16 foundation tests, while7 prior giant tests reuse the successful65/65 Supervisor checkpoint `ee62972ec8a0372f7ec682220bdc21e183b30405`, run36475157726. All65 prior tests remain unchanged in source. No claim of a new81-test run is made.
- Reused: Policy/Assessment/Snapshot1024-combination tests, RecoveryPolicy/Supervisor6144-case tests and the two old20000-iteration Snapshot/Supervisor concurrency tests. Exact methods and12 verified source SHA256 fingerprints are saved in regression-selection.json. The runner refuses reuse if a pinned primitive or test class changes. No old exhaustive suite was run separately or regenerated.

| Proven defect | Evidence and limited correction |
|---|---|
| New MonitorStep calls nonexistent Snapshot.state(Component) | compile-before.txt records4 compiler errors. Added a private field-to-component adapter inside MonitorStep; old Snapshot and its prior proofs remain unchanged. Corrected the new test's nonexistent aggregateHealth references to existing overall and added per-test registry reset. |
| MonitorConfig late+1 overflows at Long.MAX_VALUE | before-fix.txt proves config(MAX_VALUE,0) yields late=MAX_VALUE/stale=0. Cap late at MAX_VALUE-1 before enforcing stale>=late+1. Normal/default/negative/equal/extreme thresholds verified by the same focused check locally and on API25. |

| Focused coverage (6 supplied +10 added tests) | Result |
|---|---|
| Heartbeat values, nulls, all components, duplicate/regression rejection, equal-sequence/new-time and higher-sequence/equal-time, isolation/reset/retention | PASS |
| Exact LIVE/LATE/STALE boundaries; missing/null UNKNOWN; caller time clamping/future sample age0; raw threshold behavior and all liveness-to-health mappings | PASS |
| Default/invalid/equal/upper-limit configuration; event values/null/default normalization | PASS |
| Journal default/minimum capacities, repeated wrap/oldest-first order, latest identity, null append, immutable retained snapshots, clear/reuse | PASS |
| Liveness mixed snapshot/counts/null query, frozen retention after reset, explicit health apply/idempotence and missing values replacing old health | PASS |
| Complete mixed Heartbeat -> Liveness -> Health -> Supervisor -> Recommendation; exact event component/revision/time/type/health; only real transitions journaled | PASS |
|30 no-op cycles with varying escalation produce no events/revisions; later healthy/unknown cycles update correctly while prior Results stay frozen | PASS |
| Default/null-config exact threshold cycles, absent heartbeat registry, null journal counts and null health-registry conservative UNKNOWN result without writes | PASS |
| Heartbeat registry/liveness capture and journal concurrency only:200 barrier-coordinated rounds each, bounded capacity/order/uniform captures, retained values and joined test workers | PASS |

- Source/reuse review: REFERENCES.md and existing platform/Guardian/test conventions used. No third-party code or runtime dependency imported. Only two new production files needed fixes: MonitorStep and MonitorConfig. Prior Guardian primitives, MainActivity/resources/manifest/build unchanged.
- Foundation is passive and caller-driven. It intentionally updates the supplied in-memory health registry/journal; no Service/Receiver/permission/dependency/native library/production thread/timer/Handler/Executor/scheduler/polling/automatic listener/disk/network/sensor/restart/recovery execution. No watchdog, persistence/report export or source adapters. APK contains no .so.
- API25/x86,1024x600/160dpi/1GB emulator: Home/Apps/Cold Restart visually reviewed and all3 screenshots pixel-identical to prior Supervisor checkpoint. P2 Arabic/RTL/fit/navigation/Back/Home/recreation preserved, no new clipping/overlap or fabricated values. Cold Restart/settling keeps media unavailable/stopped, service records0/MediaSessions0.

| Observation | Supervisoree62972 | Monitoring0293c4e | Delta |
|---|---:|---:|---:|
| APK bytes |28,391|33,131|+4,740|
| Process PSS KB |8,944|9,129|+185|
| Launch TotalTime ms |315|382|+67|
| Views / Activities |49 /1|49 /1|0 /0|
| Observed crash / app ANR |0 /0|0 /0|none|

- WaitTime389ms; both crash buffers empty and no app ANR in either captured log. Separate single shell observations are not causal benchmarks or monitor-cycle CPU/allocation measurements; monitoring has no production UI caller. Physical T3/ARMv7/Test Station and Stable acceptance remain untested.
- Artifact11035277890 `DarbakOS-P1-TEST-0293c4e9cef7f102cacbc99c0905f9b3c95379c7`, expires2026-10-13. APK SHA256 `ab7e570b60f4a0b80d239f620e83cadbbdc4946aac8dbcbc62134374dc5b5948`; ZIP SHA256 verified against artifact digest `68c1dc2c98c4eb5f4da4f9afc79704d22b4b87e4a8f7709f2a43eb6e798fe8cd`.
- Evidence: `docs/test-evidence/p3-guardian-monitoring-20260929/` includes before/after focused proof, focused/full instrumentation, reused-proof manifest, screenshots/UI trees, lint, no-autoplay, resources/crash and comparison.json.
- **Batch closed / STOP.** No automatic Watchdog/Service or actual Recovery, no next phase.

## 2026-09-29 — P3 Guardian passive Supervisor: PASS / STOP

- Tested `ee62972ec8a0372f7ec682220bdc21e183b30405`, [run36475157726](https://github.com/abo-sultann/DarbakOS/actions/runs/36475157726), job109106904888. CI captured2026-09-28; evidence reviewed and gate closed2026-09-29. Tests/evidence only in this gate; no proven application defect or production fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi: **65/65 instrumentation PASS** in4.985s (all57 prior +8 Supervisor). **6/6 actual-tap quick-action returns PASS**.
- Supervisor remains one explicit caller-driven evaluation: Snapshot -> Assessment -> Plan. All steps are in-memory values; no recovery was executed and no automatic evaluation was added.

| Added test | Verified behavior |
|---|---|
| nullRegistryIsUnknownAtEveryBoundaryWithoutTouchingLiveState | Null registry yields valid UNKNOWN/revision0 snapshot and assessment, DIAGNOSE/all5 targets, while live FAILED registry remains unchanged |
| coldRegistryIsUnknownAtRevisionZeroAtEveryBoundary | Cold state remains UNKNOWN/revision0 with exactly5 unknown members/targets at all boundary levels |
| all6144CombinationLevelsHaveExactChainAndDoNotMutateRegistry | All1024 combinations x6 levels =6144 cases; exact snapshot health/revisions, aggregate, classification/counts, recommendation/targets; live identities/revisions unchanged |
| negativeAndHighLevelsPreserveEveryConservativePolicyBoundary | MIN_VALUE,-3,-1,0..6,100,MAX_VALUE; every uniform health and each component as sole UNKNOWN/DEGRADED/FAILED target; HEALTHY NONE, UNKNOWN DIAGNOSE, DEGRADED at most LIGHT_REPAIR, exact FAILED ladder |
| retainedResultsSurviveEveryComponentUpdateAndRepeatedReset | Results at all6 levels retain snapshot/assessment/plan and all members/values after every component/all4 health updates, later evaluations and3 resets |
| laterEvaluationReflectsChangesWhileOlderResultStaysFrozen | Each sole problem component changes to HEALTHY; new result NONE, exactly that revision increments and unrelated identities stay; old result unchanged |
| repeatedUnchangedEvaluationsKeepValuesAndEveryRevision | 100 repeats x6 levels preserve exact values and component identities/revisions; old results remain valid |
| concurrentCallersUpdatesAndResetCannotSplitAnyResult | Two test-only callers each perform10000 evaluations while a test-only writer publishes atomic uniform health changes and cold resets; all20000 results agree across snapshot/revisions/assessment/plan, retained results remain frozen and all threads are joined |

- Expected priority and step table are independent of production assessment/policy calls. Checks include exact counts/membership/target count, null queries false, source identity/health/revision preservation and snapshot/assessment aggregate agreement. All57 previous tests retained unchanged, including earlier snapshot concurrency and recovery-policy coverage.
- Concurrency test verifies only valid writer states: HEALTHY/revision1, DEGRADED/2, FAILED/3, UNKNOWN/4 or cold UNKNOWN/0. Both caller threads and the writer belong only to androidTest; no production thread is introduced. Stress coverage is not an exhaustive proof of every possible schedule.
- Source audit: incoming production change is GuardianSupervisor.java only (one capture, assessment and recommendation, immutable Result fields). No Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/scheduled polling/listener/disk/network/sensor/restart/recovery execution. MainActivity/resources/manifest/build unchanged; no production UI caller and APK contains no .so.
- REFERENCES.md and existing repository test/primitive patterns reused; no external code or dependency imported. No watchdog, heartbeat, staleness clock, logging/reporting, persistence, Safe Mode entry, rollback or source integration added.
- Home/Apps/Cold Restart screenshots visually reviewed; all3 pixel-identical to the prior recovery-policy checkpoint. Final P2 Arabic/RTL/fit/navigation/Back/Home/recreation preserved with no new clipping/overlap or fabricated values. Cold Restart and settling preserve stopped/unavailable media; app service records0/MediaSessions0.

| Observation | Recovery987884c | Supervisoree62972 | Delta |
|---|---:|---:|---:|
| APK bytes | 27,979 | 28,391 | +412 |
| Process PSS KB | 8,992 | 8,944 | -48 |
| Launch TotalTime ms | 365 | 315 | -50 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

- Launch WaitTime318ms. Both crash buffers empty; no app ANR in either captured log. Single separate emulator observations are not causal performance/CPU benchmarks or proof of zero allocation cost. Supervisor has no production UI caller. Physical T3/ARMv7/Test Station and Stable acceptance remain untested.
- Artifact10992862503: `DarbakOS-P1-TEST-ee62972ec8a0372f7ec682220bdc21e183b30405`, expires2026-10-12. APK SHA256 `a93149371a53f1b661b85148cba5322d4cd4fa83d80fe06195c5c20693503d49`. Downloaded ZIP SHA256 matches artifact digest `cbef1cfbaeba778a6cfd7d74406be7ae0732a7a8eb498f97418c56c95bd90cdc`.
- Evidence: `docs/test-evidence/p3-guardian-supervisor-20260928/` includes instrumentation, screenshots/UI trees, lint, no-autoplay, resource/crash captures and comparison.json.
- **Batch closed / STOP.** No watchdog, heartbeat, timers, logging or actual recovery started.

## 2026-09-28 — P3 Guardian recovery-policy: PASS / STOP

- Tested `987884c136aee594c43c63517d2f77f7465caa47`, [run36448995258](https://github.com/abo-sultann/DarbakOS/actions/runs/36448995258), job109018561155. Tests/evidence only in this gate; no proven application defect or production fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi: **57/57 instrumentation PASS** in4.261s (all50 prior +7 recovery-policy). **6/6 actual-tap quick-action returns PASS**.
- **No recovery was executed.** All returned steps are enum recommendations; no component/app restart, fallback/rollback, Safe Mode entry or Supervisor was introduced.

| Added test | Verified behavior |
|---|---|
| nullAssessmentOnlyDiagnosesAllUnknownAtEveryBoundary | Null -> DIAGNOSE with exactly all5 unknown targets, even with live FAILED state |
| healthyAndUnknownNeverEscalateAtAnyBoundary | HEALTHY -> NONE/zero targets; UNKNOWN -> DIAGNOSE/unknown targets, including extreme levels |
| degradedWithUnknownNeverPassesLightRepair | Mixed DEGRADED/UNKNOWN: DIAGNOSE at0/negative, LIGHT_REPAIR from1 onward; only degraded targets |
| failedUsesExactLadderAndClampsNegativeLevels | Exact FAILED ladder0..5, negatives incl.MIN_VALUE clamp to0, MAX_VALUE -> SAFE_MODE |
| everyComponentCanBeTheSoleRelevantTarget | Each component independently sole FAILED/DEGRADED/UNKNOWN target across levels0..5 |
| all6144CombinationLevelsAreDeterministicAndNonMutating | All1024 health combinations x6 levels =6144 cases; each called twice; independent expected-step table, precise target membership/count and null query false |
| retainedPlansSurviveUpdatesResetAndNewAssessments | Plans for all6 levels retain step/targets after updates to every health, reset and new assessments; old assessment/snapshot retained |

| Overall health | Level0 (also negatives) | Level1 | Level2 | Level3 | Level4/5 and upper boundary | Targets |
|---|---|---|---|---|---|---|
| HEALTHY | NONE | NONE | NONE | NONE | NONE | none |
| UNKNOWN/null | DIAGNOSE | DIAGNOSE | DIAGNOSE | DIAGNOSE | DIAGNOSE | exactly unknown |
| DEGRADED | DIAGNOSE | LIGHT_REPAIR | LIGHT_REPAIR | LIGHT_REPAIR | LIGHT_REPAIR | exactly degraded |
| FAILED | DIAGNOSE | LIGHT_REPAIR | RESTART_COMPONENT | FALLBACK_STABLE | SAFE_MODE | exactly failed |

- Boundary inputs: Integer.MIN_VALUE,-3,-1,0,1,2,3,4,5,Integer.MAX_VALUE. Frozen and live object identities/health/revisions, assessment counts/membership/overall are checked before/after recommendation. All50 previous tests preserved unchanged, including prior snapshot concurrency regression. No new test thread in this batch.
- Source review: private cloned EnumSet targets, final step and pure selection only. No new production Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution, callbacks or UI caller. Existing MainActivity/resources/manifest/build unchanged; APK has no .so.
- Existing REFERENCES.md/reuse choices and platform EnumSet/test conventions retained; no external implementation or dependency imported.
- Home/Apps/Cold Restart screenshots visually reviewed and all3 pixel-identical to previous assessment checkpoint. Final P2 content/RTL/fit/navigation/Back/Home/recreation/no-autoplay preserved, no new clipping/overlap or fabricated data. App services0/MediaSessions0 after restart/settling.

| Observation | Assessmentf1d53a6 | Recovery987884c | Delta |
|---|---:|---:|---:|
| APK bytes | 26,623 | 27,979 | +1,356 |
| Process PSS KB | 9,147 | 8,992 | -155 |
| Launch TotalTime ms | 434 | 365 | -69 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

- Launch WaitTime370ms. Crash buffers empty; no app ANR in either captured log. Single separate emulator observations are not causal performance/CPU benchmarks or proof of zero allocation cost. Policy has no production UI caller. Physical T3/ARMv7/Test Station and Stable acceptance remain untested.
- Artifact10982032472: `DarbakOS-P1-TEST-987884c136aee594c43c63517d2f77f7465caa47`, expires2026-10-12. APK SHA256 `6ace2df60f2aa8bd9770f6f46294897e98f29bf2bd30822197c95d66f1f92062`.
- Evidence: `docs/test-evidence/p3-guardian-recovery-policy-20260928/` includes instrumentation, screenshots/UI trees, lint, no-autoplay, resource/crash captures and comparison.json.
- **Batch closed / STOP.** No actual recovery, watchdog, logging, Supervisor or further work started.

## 2026-09-28 — P3 Guardian assessment: PASS / STOP

- Tested `f1d53a6e290b538430032becdc82be76d99dce17`, [run36430408151](https://github.com/abo-sultann/DarbakOS/actions/runs/36430408151), job108954834490. Verification adds tests/evidence only; no proven application defect or production fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi: **50/50 instrumentation PASS** in4.615s (all43 prior +7 assessment). **6/6 actual-tap quick-action returns PASS**.

| Added test | Verified behavior |
|---|---|
| nullSnapshotClassifiesAllFiveUnknownWithoutTouchingLiveState | Null input gives all5 UNKNOWN and UNKNOWN overall while live FAILED states remain untouched |
| coldSnapshotClassifiesUnknownAndPreservesRevisionZero | Cold snapshot gives all5 UNKNOWN, preserves exact state identities and revision0 |
| allHealthyHasFiveHealthyMembersAndHealthyOverall | All5 HEALTHY, healthyCount5, other counts0 and HEALTHY overall; revisions stay1 |
| all1024CombinationsHaveExactExclusiveMembershipCountsAndOverall | All4^5 combinations: each component belongs to exactly one correct bucket; exact counts sum to5; overall matches independent priority and source snapshot |
| retainedAssessmentSurvivesAllUpdatesResetAndNewSnapshots | Retained mixed assessment and old input remain valid after every component/all4 health updates, new captures/assessments and3 resets |
| repeatedAssessmentUsesFrozenInputRatherThanCurrentRegistry | Same frozen mixed input produces consistent counts/membership/overall after live state changes to HEALTHY; new input reflects HEALTHY |
| nullComponentQueriesAreFalseAndPreserveAllAssessmentValues | All4 null membership queries return false repeatedly; counts/membership/overall and registry snapshot identities remain unchanged |

- Assessment helper captures live and frozen object identities plus independent health/revision values before creation and checks them afterward. Exhaustive coverage exercises every component in every health state. All43 prior tests retained, including snapshot concurrency regression; this batch adds no test thread.
- Complete source review: private final cloned EnumSets, no exposed mutable collections, and pure classification/count/membership methods. No new production Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution, callbacks, recovery action or UI caller. MainActivity/resources/manifest/build unchanged; APK contains no .so.
- REFERENCES.md and recorded reuse choices remain applicable; existing snapshot/platform EnumSet and instrumentation patterns reused. No external implementation/dependency imported.
- Home/Apps/Cold Restart screenshots visually reviewed and all3 pixel-identical to prior snapshot checkpoint. Final P2 content/RTL/fit/navigation/Back/Home/recreation/no-autoplay preserved; no new clipping/overlap, temporary wording or fabricated data. App service records0/MediaSessions0 after restart/settling.

| Observation | Snapshot3709e35 | Assessmentf1d53a6 | Delta |
|---|---:|---:|---:|
| APK bytes | 25,327 | 26,623 | +1,296 |
| Process PSS KB | 9,055 | 9,147 | +92 |
| Launch TotalTime ms | 356 | 434 | +78 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

- Launch WaitTime437ms. Both crash buffers empty; no app ANR in either captured log. Separate single emulator observations are not causal performance/CPU benchmarks or proof of zero allocation cost. Assessment is not called by production UI. Physical T3/ARMv7/Test Station and Stable acceptance remain untested.
- Artifact10973700564: `DarbakOS-P1-TEST-f1d53a6e290b538430032becdc82be76d99dce17`, expires2026-10-12. APK SHA256 `160da01d460decd1f05dce24fe555e54c5ee4fd94676f76837cc4dcf047348ea`.
- Durable evidence: `docs/test-evidence/p3-guardian-assessment-20260928/` includes instrumentation, screenshots/UI trees, lint, no-autoplay, resource/crash captures and comparison.json.
- **Batch closed / STOP.** No watchdog, logging, recovery or other part started.

## 2026-09-28 — P3 Guardian snapshot: PASS / STOP

- Tested `3709e356f0fdc623b86f7895b18c1ff1ef553f05`, [run36427535535](https://github.com/abo-sultann/DarbakOS/actions/runs/36427535535), job108945090176. Added7 focused tests, preserved all36 previous tests, and fixed one proven defect in the incoming snapshot increment.
- **Proven defect/fix:** On unchanged incoming `deb45ec`, a Java17 concurrent-update probe captured CORE=FAILED/revision242 but overall=HEALTHY. Separate registry reads and later aggregation did not freeze one instant. `capture` now holds the registry's existing monitor across every read and aggregate calculation. Same probe passes20000 captures after the fix. Before/after output and reproducer are checkpointed; this is local JVM evidence, not an Android app crash.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi: **43/43 instrumentation PASS** in4.3s (all36 prior +7 snapshot). **6/6 actual-tap quick-action returns PASS**.

| Added test | Verified behavior |
|---|---|
| nullRegistryReturnsColdValuesWithoutChangingLiveState | Null yields UNKNOWN overall/all5 cold revision0 values without changing live FAILED snapshots |
| coldCapturePreservesExactUnknownSnapshotsAtRevisionZero | Cold capture retains exact component objects and UNKNOWN0 values |
| all1024CombinationsCaptureExactStatesAndCorrectAggregate | Every4^5 health combination across all5 components, exact field-to-component identity, independent precedence check and no mutation/revision changes; preceding capture remains unchanged |
| retainedSnapshotSurvivesEveryComponentUpdateAndRepeatedReset | Mixed retained snapshot survives all4 updates to each component and3 resets; fresh cold captures UNKNOWN0 while retained objects/values stay unchanged |
| repeatedUnchangedCapturePreservesComponentIdentitiesAndRevisions | Repeated mixed DEGRADED/UNKNOWN captures retain equal values and same component identities/revisions |
| newCaptureReflectsRecoveryWhileOldSnapshotRemainsUnchanged | Every component recovers from FAILED and DEGRADED; new capture is HEALTHY, only explicit update advances that component revision, older snapshot and other component identities retained |
| concurrentUpdatesAndResetCannotSplitSnapshotOrAggregate | 20000 captures during test-only atomic component updates/reset; every captured component health/revision and overall describe the same state; writer stopped/joined before teardown |

- The concurrency writer exists only in androidTest/local reproduction, never in the application. Production fix uses existing synchronization and verified GuardianPolicy; no new thread or scheduled work. Tests reset singleton before/after, and exact health/revision values are captured independently for retention assertions.
- Home/Apps/Cold Restart screenshots visually reviewed and all3 pixel-identical to prior aggregate-policy checkpoint. Final P2 content/RTL/fit/navigation/Back/Home/recreation/no-autoplay unchanged; no new clipping/overlap, temporary wording or fabricated data. App service records0/MediaSessions0 after restart/settling.
- No new production Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution, callbacks or UI caller. MainActivity/resources/manifest/build unchanged; APK contains no .so. REFERENCES.md reviewed; reused the registry monitor, aggregate policy and existing instrumentation patterns, with no external code/dependency.

| Observation | Policye42f2d6 | Snapshot3709e35 | Delta |
|---|---:|---:|---:|
| APK bytes | 24,987 | 25,327 | +340 |
| Process PSS KB | 8,958 | 9,055 | +97 |
| Launch TotalTime ms | 371 | 356 | -15 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

- Launch WaitTime361ms. Both crash buffers empty; no app ANR in either captured log. Separate single emulator observations are not causal performance/CPU benchmarks or proof of zero computation/allocation cost. Production UI does not call snapshot. Physical T3/ARMv7/Test Station and Stable acceptance remain untested.
- Artifact10972606111: `DarbakOS-P1-TEST-3709e356f0fdc623b86f7895b18c1ff1ef553f05`, expires2026-10-12. APK SHA256 `837eadff701f289592c2b6d45a9795e92cb8f50b73e4ea8143e49c855405a96b`.
- Durable evidence: `docs/test-evidence/p3-guardian-snapshot-20260928/` includes instrumentation, screenshots/UI trees, lint, no-autoplay, resource/crash captures, comparison.json and before/after race reproduction.
- **Batch closed / STOP.** No watchdog, logging or other part started.

## 2026-09-28 — P3 Guardian aggregate-policy: PASS / STOP

- Tested `e42f2d6d9355afabd6c4d7830c4182dadbed2685`, [run36401135032](https://github.com/abo-sultann/DarbakOS/actions/runs/36401135032), job108859015062. Incoming GuardianPolicy only derives overall health from explicit component values. This gate adds tests/evidence only; no proven application defect or production fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi: **36/36 instrumentation PASS** in4.142s: all29 prior tests plus7 policy tests. **6/6 actual-tap quick-action returns PASS**.

| Added test | Verified behavior |
|---|---|
| nullRegistryIsUnknownWithoutMutatingExistingRegistry | Null input returns UNKNOWN, even with FAILED/DEGRADED states in the existing singleton; singleton remains unchanged |
| allUnknownColdBootRemainsUnknownAtRevisionZero | Cold registry aggregates UNKNOWN; every component remains UNKNOWN revision0 |
| allHealthyIsHealthyWithoutRevisionChanges | All HEALTHY aggregates HEALTHY; each revision remains1 |
| everyComponentCanBeTheSoleFailedDegradedOrUnknownSource | Each of5 components independently supplies the sole FAILED, DEGRADED or UNKNOWN state among healthy peers |
| all1024CombinationsRespectPrecedenceWithoutMutation | Exhaustive4^5 combinations: FAILED > DEGRADED > UNKNOWN > HEALTHY; covers failed regardless of peers and degraded with unknown; independent precedence ranking, exact outcome counts1/31/211/781 |
| eachComponentRecoversFromFailedAndDegradedToAllHealthy | Each component recovers from both problem states to all HEALTHY; only explicit update increments revision, retained problem snapshot stays unchanged |
| mixedRecoveryAndResetFollowRemainingPriorityWithoutCachedHealth | Mixed FAILED/DEGRADED/UNKNOWN recovers through remaining priority to HEALTHY; cold reset immediately yields UNKNOWN |

- Every aggregate assertion runs twice and checks all5 snapshot identities plus separately captured health/revision values before/after. All1024 combinations are checked without registry mutation; tests reset singleton before/after. All29 previous Core/Guardian/Shell tests retained unchanged.
- Home/Apps/Cold Restart screenshots visually reviewed and all3 pixel-identical to previous registry checkpoint. Final P2 content/RTL/fit/navigation/Back/Home/recreation/no-autoplay preserved; no new clipping/overlap, temporary text or invented live data. Stopped/unavailable media persists after restart/settling; app service records0/MediaSessions0.
- Complete policy source reviewed: synchronous reads/enum decisions only, no state writes, timers, callbacks or Android calls. No Service/Receiver/permission/dependency/native library/thread/Handler/Executor/disk/network/sensor/restart/background execution. No production UI caller. MainActivity/resources/manifest/build unchanged; downloaded APK contains no .so.
- REFERENCES.md and prior recorded reuse remain unchanged; reused the existing instrumentation and immutable snapshot test patterns. No new external component or dependency introduced.

| Observation | Registry7ef7037 | Policye42f2d6 | Delta |
|---|---:|---:|---:|
| APK bytes | 24,751 | 24,987 | +236 |
| Process PSS KB | 8,984 | 8,958 | -26 |
| Launch TotalTime ms | 397 | 371 | -26 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

- Launch WaitTime379ms. Both crash buffers empty; no app ANR in captured logs. Separate single emulator observations are not causal performance/CPU benchmarks or evidence of zero computation/allocation cost. No background execution is introduced; the policy is not invoked by production UI. Physical T3/ARMv7/Test Station and Stable acceptance remain untested.
- Artifact10960945564: `DarbakOS-P1-TEST-e42f2d6d9355afabd6c4d7830c4182dadbed2685`, expires2026-10-12. APK SHA256 `a6b3c6f1f820e4c9b968b9fc697ca81460b94b8cbc062d1b454d604f81c6119b`.
- Durable evidence: `docs/test-evidence/p3-guardian-aggregate-20260928/` includes instrumentation, screenshots/UI trees, lint, no-autoplay, resource/crash captures and comparison.json.
- **Batch closed / STOP.** No watchdog, logging or other part started.

## 2026-09-28 — P3 Guardian component-registry: PASS / STOP

- Tested `7ef7037e05f004280059ccec6d78bd19cf9a519b`, [run36399872441](https://github.com/abo-sultann/DarbakOS/actions/runs/36399872441), job108854932827. Incoming GuardianRegistry owns explicit per-component health; this gate adds tests/evidence only. No proven application defect or production fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi: **29/29 instrumentation PASS** in4.089s (9 CorePublish,3 CoreState,7 GuardianRegistry,5 GuardianState,5 Shell). All22 previous tests retained unchanged. **6/6 actual-tap quick-action returns PASS**.

| Added test | Verified behavior |
|---|---|
| coldBootInitializesAllFiveComponentsAndStableSnapshots | Exactly CORE/HOME/NAVIGATION/MEDIA/VEHICLE, each UNKNOWN revision0, distinct component snapshots, repeat reads and singleton identity stable |
| everyComponentTransitionIsIsolatedAndAdvancesExactlyOnce | All60 distinct transitions (5 components x12 health transitions); returned state equals snapshot, new identity, exactly +1 target revision, all other component identities and every retained value unchanged |
| sameHealthPreservesIdentityRevisionAndAllOtherComponents | Same-state updates repeated across all components/all4 health values; identities/revisions and other components unchanged |
| nullHealthPreservesIdentityRevisionAndAllOtherComponents | Null health across all components/all4 values; identities/revisions/content unchanged |
| repeatedColdResetReplacesEverySnapshotAndRetainsOldStates | Mixed nonzero-revision states,3 resets; every component gets a fresh UNKNOWN revision0 snapshot; retained old values unchanged |
| nullComponentLookupAndUpdatesDoNotAlterRegistryContents | Null lookup and null-component updates with all4 health values/null return UNKNOWN0 without changing any registry snapshot/content |
| eachComponentCanRecoverFromFailedWithoutAffectingOthers | Every component FAILED -> HEALTHY exactly +1 with new snapshot; old FAILED state and all other components unchanged |

- Tests reset singleton before/after each test; captured health/revision values independently verify retained objects. Synthetic test states never bind to UI. Existing Core publication/reset/listeners/null, GuardianState, Activity/recreation and navigation checks retained.
- Home/Apps/Cold Restart screenshots visually reviewed and all3 pixel-identical to prior Guardian health checkpoint; final P2 content/RTL/fit unchanged, no new clipping/overlap or fabricated values. Home/Back/quick actions and stopped/unavailable media remain correct after cold restart and settling; app service records0/MediaSessions0.
- Full source review: GuardianRegistry uses existing GuardianState and platform java.util EnumMap/Map with synchronized in-process methods only. No new Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution or listener callbacks. No references from production UI to registry. MainActivity/resources/manifest/build unchanged; downloaded APK contains no .so.
- REFERENCES.md and recorded prior-project reuse reviewed; reused existing instrumentation/test conventions. No new external implementation or dependency needed for this verification. Registry performs no monitoring, inference or self-healing action.

| Observation | Healthe994833 | Registry7ef7037 | Delta |
|---|---:|---:|---:|
| APK bytes | 23,839 | 24,751 | +912 |
| Process PSS KB | 8,905 | 8,984 | +79 |
| Launch TotalTime ms | 364 | 397 | +33 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

- Launch WaitTime401ms. Both crash buffers empty; no app ANR in captured logs. Separate single emulator observations are not causal performance/CPU benchmarks or proof of zero allocation cost; production UI does not invoke registry. Physical T3/ARMv7/Test Station and Stable acceptance remain untested.
- Artifact10959049545: `DarbakOS-P1-TEST-7ef7037e05f004280059ccec6d78bd19cf9a519b`, expires2026-10-12. APK SHA256 `a3125c6b2828f5d9046349a02a76ccbb3acc6df349de7fceaa73559e704b76ed`.
- Durable evidence: `docs/test-evidence/p3-guardian-registry-20260928/` includes instrumentation, screenshots/UI trees, lint, no-autoplay, resource/crash captures and comparison.json.
- **Batch closed / STOP.** No watchdog, logging or other part started.

## 2026-09-28 — P3 Guardian health-state foundation: PASS / STOP

- Tested `e9948337345ee7ec0d810b48508bb0dbe388affb`, [run36395918000](https://github.com/abo-sultann/DarbakOS/actions/runs/36395918000), job108842149353. Incoming GuardianState is pure immutable state; this gate adds tests/evidence only. No proven application defect or production fix.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi: **22/22 instrumentation PASS** in3.898s: all17 prior tests plus5 Guardian tests. **6/6 actual-tap quick-action returns PASS**.

| Added test | Verified behavior |
|---|---|
| coldBootIsUnknownAtRevisionZero | UNKNOWN and revision0 |
| everyDistinctTransitionAdvancesExactlyOnceAndRetainsPriorState | All12 distinct directed transitions among4 states produce a new object and exactly one revision increment; prior health/revision retained |
| sameHealthPreservesIdentityAndRevisionInEveryState | Repeated same-state calls in each state preserve object identity and revision, including nonzero revisions |
| nullPreservesIdentityAndRevisionInEveryState | Null in each state preserves object identity, health and revision |
| failedRecoveryIsAnImmutableStateChangeOnly | UNKNOWN0 -> HEALTHY1 -> DEGRADED2 -> FAILED3 -> HEALTHY4; all prior instances unchanged, fresh coldBoot remains UNKNOWN0 |

- Existing Core publish/listeners/reset/revision/null, Activity recreation, Home/Apps/RTL/fit/Back/Home/Cold Restart tests retained unchanged. No autoplay, no selected track/fabricated live data, zero app Service records/MediaSessions.
- Home/Apps/Cold Restart screenshots visually inspected: no clipping/overlap and accepted final P2 content preserved. Apps and cold-restart Home are pixel-identical to prior checkpoint. Initial Home differs only in Settings button bounds[16,16][115,72] (5393pixels): UI tree confirms focused=true versus baseline false; existing focus selector explains color. All pixels outside that button are identical. This is captured focus state, not a Guardian/UI change.
- Reviewed complete GuardianState: final immutable fields/private constructor, enum, coldBoot and withHealth only; no imports or Android calls. No Service/Receiver/permission/dependency/native library/thread/timer/Handler/Executor/disk/network/sensor/restart/background execution. No production references to GuardianState outside its own file, no UI binding or monitoring. MainActivity/resources/manifest/build unchanged. Downloaded APK contains no .so.
- REFERENCES.md reviewed; existing local test structure/platform instrumentation reused. No new component implementation, external code or dependency imported. No watchdog/logging/recovery action introduced; recovery is only a state value transition.

| Observation | Resetcdc64b3 | Guardiane994833 | Delta |
|---|---:|---:|---:|
| APK bytes | 23,303 | 23,839 | +536 |
| Process PSS KB | 9,143 | 8,905 | -238 |
| Launch TotalTime ms | 317 | 364 | +47 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

- Launch WaitTime368ms; both crash captures empty; no app ANR in captured logs. Single separate emulator observations do not establish causal performance/CPU changes or literal zero allocation cost. Guardian has no scheduled work and is not invoked by production UI. Physical T3/ARMv7/Test Station and Stable acceptance remain untested.
- Artifact10958620040: `DarbakOS-P1-TEST-e9948337345ee7ec0d810b48508bb0dbe388affb`, expires2026-10-12. APK SHA256 `bfb056ca837de3c39aa08349f9259399681eec913c481a9ea12f2f137a161a12`.
- Durable evidence: `docs/test-evidence/p3-guardian-health-20260928/`: instrumentation, screenshots/UI trees, lint, no-autoplay, resources/crash captures and comparison.json.
- **Batch closed / STOP.** No watchdog, logging or other batch started.

## 2026-09-28 — P3 cold-reset publication: PASS / STOP

- Tested `cdc64b3b201e7e6f2de570a0c82b099114e186b5`, [run36362412922](https://github.com/abo-sultann/DarbakOS/actions/runs/36362412922), job108742075127. Incoming production change routes resetForColdBoot through publish(coldBoot()); this verification adds tests/evidence only. No application defect found or additional production fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi: **17/17 instrumentation PASS** in2.27s (9 CorePublish,3 CoreState,5 Shell). All14 previous tests retained.

| Added test | Verified behavior |
|---|---|
| resetPublishesOnceToEachUniqueListenerInline | Two unique listeners, one duplicate registration, exactly one callback each; synchronous caller-thread delivery before reset returns; both receive the same object as snapshot; revision0/all UNAVAILABLE/media stopped; retained old state unchanged |
| removedListenerReceivesNoColdReset | Both receive an ordinary publication; after removal only remaining listener receives cold reset; exact counts and cold state checked |
| repeatedResetsDeterministicallyPublishFreshColdSnapshots | Three resets produce exactly three distinct cold snapshots with exact callback/snapshot identity; all retained reset values stay cold |

- Existing revision increments, immutable prior snapshots, publish identity, duplicate registration, listener removal, null silence, cold boot and Activity/recreation checks retained. Test teardown removes listeners before resetting; synthetic state stays in tests only.
- **6/6 actual-tap quick-action returns PASS**; Home/Apps/RTL/fit/navigation/Back/Home/recreation/Cold Restart/no-autoplay preserved. Immediate and settled restart show unavailable/no-route/no-track/stopped states, zero app service records and zero app MediaSessions.
- Home and Apps screenshots visually reviewed: no clipping/overlap or fabricated live values/temporary wording. Home/Apps/cold-restart screenshots are pixel-identical to prior publish checkpoint.
- MainActivity/resources/manifest/build configuration unchanged. Reviewed reset delegates to existing synchronous in-process publication only: no new Service, permission, dependency, native library, disk/network/sensor/thread/background execution. Downloaded APK has no .so.

| Observation | Publish31ce9bc | Resetcdc64b3 | Delta |
|---|---:|---:|---:|
| APK bytes | 23,319 | 23,303 | -16 |
| Process PSS KB | 9,180 | 9,143 | -37 |
| Launch TotalTime ms | 475 | 317 | -158 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

- Launch WaitTime323ms; crash buffers empty and no app ANR observed in captured logs. These are separate single emulator observations, not causal performance improvements or CPU benchmarks; physical T3/ARMv7 acceptance remains untested.
- Artifact10946202227: `DarbakOS-P1-TEST-cdc64b3b201e7e6f2de570a0c82b099114e186b5`, expires2026-10-12. Internal artifact name does not describe user-facing copy. APK SHA256: `7c3e4976a7fa6626bf1a2efb3824102d7de407af7530d9c01a6d93976734f98f`.
- Durable evidence: `docs/test-evidence/p3-cold-reset-20260928/` includes instrumentation, UI trees/screenshots, no-autoplay, resource captures, lint XML and comparison.json. Full workflow logs/artifact remain linked above.
- **Batch closed / STOP.** No Guardian, logging or other P3 work started. No Stable or physical-device validation claimed.

## 2026-09-28 — P3 Core publish/subscription: PASS / STOP

- Tested `31ce9bc83cd37760433da482c2f4cce3cd793673`, [run36361519633](https://github.com/abo-sultann/DarbakOS/actions/runs/36361519633), job108739492043. This verification gate changes tests/docs only; no application defect found or fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi,1GB RAM/software GPU: **14/14 instrumentation PASS** in3.978s (all8 prior tests plus6 focused publish/subscription tests).

| Focused test | Verified behavior |
|---|---|
| revisionsAdvanceWithoutMutatingPriorStates | cold revision0, successive immutable updates1/2; every state field checked and prior states retained |
| publishUpdatesSnapshotAndCallsListenersInlineWithExactState | snapshot and both listener arguments are the exact published object; callbacks complete before publish returns on the publishing thread |
| duplicateRegistrationDoesNotMultiplyCallbacks | same listener registered twice receives one callback per publication, revisions advance1/2 |
| removalStopsOnlyTheRemovedListener | removed listener stops, remaining listener and snapshot updates continue |
| nullPublishPreservesSnapshotRevisionAndSilence | null preserves exact existing snapshot/revision and produces no callback |
| coldResetClearsPublishedAvailabilityPlaybackAndRevision | after published revision2/playing state, reset returns revision0/all unavailable/stopped without mutating retained state |

- Existing cold-boot assertions strengthened with revision0. Tests remove listeners and reset store in teardown; synthetic values exist only in instrumentation, never bound to UI. No new thread/Handler/Executor was added; current-thread identity is inspected inside synchronous callbacks.
- **6/6 actual-tap quick-action returns PASS**, Home/Apps/RTL/fit/Back/Home/recreation/Cold Restart/no-autoplay preserved. Restart immediately/after settling shows truthful unavailable/no-route/no-track/stopped states; app services0/media sessions0.
- Actual Home/Apps screenshots reviewed: no clipping/overlap or fabricated UI values; Home/Apps/cold-restart images pixel-identical to prior Core foundation (and accepted P2 images).
- MainActivity/resources/manifest/build dependency diffs against previous checkpoint are empty. Core uses java.util in-process listener copies and immutable fields only: no Service, permission, runtime dependency, native library, disk/network/sensor/background execution. No .so in downloaded APK. No source/UI binding introduced.

| Observation | Foundation6bcc623 | Publish31ce9bc | Delta |
|---|---:|---:|---:|
| APK bytes | 22,607 | 23,319 | +712 (+3.15%) |
| Process PSS KB | 9,007 | 9,180 | +173 (+1.92%) |
| Launch TotalTime ms | 301 | 475 | +174 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

Separate single emulator snapshots, not controlled benchmarks. The larger launch/PSS observations do not establish a causal regression; CPU performance is not established. Raw diagnostics retained; no T3/ARMv7 performance claim.

- Downloaded APK SHA256 verified: `68fdd84556f903a64b7ac4c59cec869f4609f54a5d8ce13d4adf173f28b78060`.
- Durable [evidence](docs/test-evidence/p3-publish-20260928/): comparison JSON, Home/Apps/restart PNG, UI XML,14-test/6-flow results, no-autoplay/services/sessions/audio, lint, APK summary and resource/crash outputs. Full artifact10946001811 expires2026-10-12.
- Existing REFERENCES/platform/test harness reused; no component built from scratch. Local source guard, Python syntax and whitespace PASS. No Stable or physical-device acceptance.

**Disposition: complete, commit/push and STOP. No Guardian, logging/reporting, source adapters or further P3 batch.**

## 2026-09-27 — P3 Core State foundation: PASS / STOP

- Tested `6bcc62319d8690ee1316655de61bf15106669192`, [run36344308617](https://github.com/abo-sultann/DarbakOS/actions/runs/36344308617), job108690318096. This gate added tests only; no application defect found or application fix needed.
- Build/Lint PASS: 0 errors,17 existing warnings. API25/x86,1024x600/160dpi,1GB RAM/software GPU: **8/8 instrumentation PASS** in3.657s, comprising all5 prior UI tests and3 focused CoreState tests.
- Core tests: coldBoot explicitly makes speed/navigation/vehicle UNAVAILABLE and mediaPlaying=false; singleton reads preserve snapshot identity, reset creates a new cold snapshot and leaves held immutable snapshots valid; fresh Activity resets core state, while recreation preserves the same snapshot and Home remains selected.
- Existing **6/6 actual-tap quick-action returns PASS**, all sections/Apps/RTL/Back/Home/recreation/fit preserved. Cold process restart lands on Home with honest unavailable/no-route/no-track/stopped states; settled state remains unchanged, app services0/media sessions0.
- Home/Apps screenshots visually reviewed and pixel-identical to P2 baseline27e0e79; cold-restart Home also pixel-identical. No clipping/overlap, temporary wording, fabricated values or autoplay.
- Source/manifest/build review and guards: no new permission, Android Service, receiver, runtime dependency or native library; APK contains no .so. Core uses immutable Java fields and synchronized in-memory snapshot/reset only, with no thread/timer/task, disk, network or playback work. P2 UI remains static and is not yet bound to a live source; this verifies only the requested internal foundation.

| Observation | P2 baseline27e0e79 | Core6bcc623 | Delta |
|---|---:|---:|---:|
| APK bytes | 20,925 | 22,607 | +1,682 (+8.04%) |
| Process PSS KB | 8,890 | 9,007 | +117 (+1.32%) |
| Launch TotalTime ms | 368 | 301 | -67 |
| Views / Activities | 49 / 1 | 49 / 1 | 0 / 0 |
| Crash / app ANR observed | 0 / 0 | 0 / 0 | none |

Separate single emulator observations, not controlled benchmarks: do not attribute PSS/startup variation solely to Core State. CPU performance is not established; raw CPU/gfx diagnostics retained, no 0% CPU claim or physical T3/ARMv7 extrapolation.

- Downloaded APK SHA256 verified: `276558073babaa7c20122c2cea4398318a8d72635b64f499d986fce6586a91d8`.
- Durable [evidence](docs/test-evidence/p3-core-state-20260927/): comparison JSON, Home/Apps/restart PNG, UI XML,8-test/6-flow outputs, no-autoplay/services/sessions/audio, lint, summary and raw resource/crash observations. Full artifact10939119217 expires2026-10-11.
- Existing REFERENCES/platform and test-harness choices reused; no component built from scratch in this verification gate. Local source guard, Python syntax, whitespace and exact unchanged resource/manifest/dependency diffs PASS.

**Disposition: Core State foundation complete; checkpoint and STOP. No Guardian, logging/reporting or other P3 work. No Stable/T3 acceptance.**

## 2026-09-27 — P2 final-product UI verification: PASS / STOP

- Owner instruction: current Darbak OS interface is the final-product UI, not a disposable prototype. Unconnected sources display truthful unavailable/stopped states. This supersedes earlier user-facing TEST-copy requirements; historical test records below remain historical.
- Tested `27e0e79236522cd175b49f38317c9b7b533e8122` in [run36343420707](https://github.com/abo-sultann/DarbakOS/actions/runs/36343420707), job108687799511.
- Proven defect in conversion: three Apps buttons were enabled despite having no listeners/backend. Disabled and visually dimmed those actions, retaining intended labels/layout and existing unavailable message. No backend added.
- Updated obsolete source/instrumentation/smoke TEST-copy requirements to exact final-state assertions. Added rejection of temporary wording in all string values and every captured visible text/accessibility description. Behavioral navigation, Back/Home, fit, RTL, cold restart and no-autoplay assertions retained.
- Build/Lint PASS: 0 errors,17 warnings. API25/x86,1024x600/160dpi,1GB RAM/software GPU: **5/5 instrumentation PASS** in3.214s (4 existing plus Apps fit/RTL/unavailable actions).
- **6/6 actual-tap quick-action returns PASS**. All sections/Settings visited. Apps actions appear in Apps only, labels `الأخيرة` / `المفضلة` / `إدارة التطبيقات`, each disabled and at least56px high. New instrumentation validates right-to-left order, full text/view fit and Return Home hiding Apps actions.
- Visual review: actual Home and Apps screenshots have no clipping/overlap, Arabic/RTL readable. Home shows unavailable speed, no active route with navigation unavailable, no selected media/stopped, and vehicle data unavailable. No fake turn/distance/time/track/position/normal-vehicle readings. No TEST/experimental/preview wording in UI or captured accessibility text.
- Cold Restart returns to Home; immediate/settled states remain unavailable/stopped. App services0 and MediaSessions0. No playback implementation or background behavior introduced. Crash buffer empty; no app ANR detected.
- APK20,925 bytes; downloaded SHA256 verified: `60e793776f91ae2e29334e8c1ddd74a48258fd8b00aeebf7d6e8767b770b0cab`.
- Durable [evidence](docs/test-evidence/p2-final-ui-20260927/): Home/Apps/restart PNG, all UI XML,5-test/6-flow results, no-autoplay/services/sessions/audio, summary, lint and runtime observations. Full artifact10939787622 expires2026-10-11.
- Local source guard, Python syntax and whitespace PASS. Reused existing Android Views/disabled-button capability and recorded REFERENCES/test harness; no new dependencies/services/backend.
- Final-product UI acceptance here is emulator-stage only; physical T3/ARMv7 and Stable release acceptance remain outstanding. Internal resource IDs/package/artifact names retaining test history are not user-visible labels.

**Disposition: complete, commit/push and STOP. No P3 or additional backend/batch.**

## 2026-09-27 — P2 stopped Media verification: PASS / STOP

- Tested `abc0b3dd756e39498d23df1597b0f0dd7ac8db04`, [run36342407385](https://github.com/abo-sultann/DarbakOS/actions/runs/36342407385), job108684931636. Application remains7ce4930; only smoke expectations/verification and documentation changed.
- Initial run36341691472 passed Build/Lint and4 tests, but failed obsolete `خامل` smoke expectation. Corrected to exact TEST track/stopped position; no application defect found or app change made.
- Build/Lint PASS: 0 errors,17 warnings (previous16 plus unused media_idle from this increment; no unrelated cleanup).
- API25/x86,1024x600/160dpi,1GB RAM/software GPU: **4/4 instrumentation PASS** in2.387s; text/view fit, RTL,16dp gap, navigation/recreation preserved.
- **6/6 actual-tap quick-action returns PASS**, correct destinations/selected tabs with Android Back and Return Home. Exact media, navigation TEST examples, unavailable speed/navigation, test-only normal vehicle, full stale warning and global TEST asserted after every return.
- **Cold Restart PASS:** force-stop from Settings then launch lands on Home; all labels checked immediately and after2s settling. Track/position unchanged, `متوقف (تجريبي)` remains visible. App service records0; app MediaSessions0. System telecom has one inactive unrelated session; not attributed to Darbak. Audio diagnostics retained.
- No-autoplay conclusion combines runtime observations with inspected MainActivity/manifest: static TextViews only, no playback/MediaSession/audio-focus code, service, receiver, permission or runtime dependency. This verifies the current static TEST shell, not a future media engine or physical T3.
- Visual review of actual Home and cold-restart Home: no clipping/overlap. `يا طريق • مقطع تجريبي` and `01:24 / 04:10 • متوقف (تجريبي)` fit fully; global test badge remains visible. Track y382–410, position/state y416–444, Media button y452–508; bottom navigation begins y528. All quick targets remain56px high.
- APK20,529 bytes; downloaded SHA256 verified: `3d7b23b011ecc3fe71e43b01f507912df49e9bc7cb6941fe4ad8d51627612b81`. Launch386ms (single emulator observation). Crash buffer empty; no app ANR found by smoke.
- Durable [evidence](docs/test-evidence/p2-media-20260927/) includes prior failure, Home/restart PNG/XML, returned Home XML, no-autoplay result and raw services/sessions/audio,6-flow/4-test results, lint, APK summary and launch/memory/crash. Full artifact10939271595 expires2026-10-11.
- Local source guard, Python syntax and whitespace PASS. Existing REFERENCES/platform/testing choices reused; no new component. No physical hardware/ARMv7/Stable acceptance.

**Disposition: complete, checkpoint and STOP. No P3 or next batch.**

## 2026-09-27 — Stopped Media verification setup (historical; resolved above)

- Mainea52d9b / implementation7ce4930: run36341691472 passed Build/Lint and4 instrumentation tests, but smoke stopped at outdated `خامل` expectation after Media changed to explicit stopped TEST preview.
- Actual screenshot reviewed: track and position/test qualifier fit; no clipping/overlap. No app defect found.
- Correct smoke expectation to exact TEST track/stopped position; preserve prior-state regressions. Add cold-restart/settled Home label checks and capture/assert no app ServiceRecord or MediaSession, plus audio diagnostics.
- MainActivity/manifest reviewed: static views only, no playback/audio-focus/MediaSession/service code or runtime dependency. Existing REFERENCES/platform/test workflow reused; no new component.
- Full rerun pending. Same bounded verification batch; finish evidence then STOP.

## 2026-09-27 — P2 navigation-card verification: PASS / STOP

- Verified code `b054d7d5a6e1d9f82d5fbe290a5cf415a418cbb3` using [run36325924001](https://github.com/abo-sultann/DarbakOS/actions/runs/36325924001), job108638552766. Existing successful run inspected, not rerun. Main2612b6a differs only in the task document; application/build/test input diff is empty.
- Build/Lint PASS: 0 errors,16 warnings (previous15 plus unused navigation_detail after replacement; no unrelated cleanup).
- API25/x86,1024x600/160dpi,1GB RAM/software GPU: **4/4 instrumentation PASS**,2.817s, including text/view fit,16dp gap, RTL, section navigation and recreation.
- **6/6 actual-tap quick-action round trips PASS**: Map/Media/Vehicle via Android Back and Return Home. Existing navigation/Settings/cold restart smoke passed. Touch targets remain56px high.
- Actual Home screenshot reviewed: no clipping/overlap. Maneuver is bold24sp; ETA/distance20sp below; gold unavailable state clearly separated. Maneuver bounds y195–228, ETA y234–262, unavailable y270–298, all inside the navigation card y88–324.
- Semantic check: `بعد 800 م • انعطف يمينًا (تجريبي)` and `12 د • 7.4 كم (تجريبي)` each carry their own test qualifier; global `نسخة اختبار • بيانات تجريبية` and `الملاحة • غير متاحة` remain visible. These are static TEST examples, not live guidance.
- Post-run assertions against eight captured Home UI trees (launch, six quick-action returns, cold restart) passed for both navigation examples, unavailable navigation/speed, idle media, test-only normal vehicle summary, full stale warning and global TEST badge. Retained JSON distinguishes this evidence inspection from a new runtime test.
- Crash buffer empty; no app ANR found. APK20,213 bytes; downloaded SHA256 verified: `8c838a340926fe45edee2ea5364d993fa8177bed941f963b243d5dacbb07c166`. Launch408ms, single emulator observation only.
- Durable [evidence](docs/test-evidence/p2-navigation-20260927/): Home PNG, initial/return/restart UI XML, semantic-review JSON,6-flow JSON,4-test output, summary, lint XML, launch/memory/crash. Full artifact10933768031 expires2026-10-11.
- Source guard and whitespace checks PASS. No application defect found, no app/test/build change needed. Existing REFERENCES/platform choices reused; no new component or dependency.

**Disposition: complete, checkpoint and STOP. No P3/next batch or T3/Stable acceptance.**

## 2026-09-27 — P2 quiet Home summary: PASS / STOP

- Tested code `9ac0b10586a1fa76a165717a064b90a69d3f74e3`, [run36324401688](https://github.com/abo-sultann/DarbakOS/actions/runs/36324401688), job108634243574.
- Proven regression: initial d75e683 run36324136031 failed the existing fit test (1/4 failures), with the vehicle title outside its card after the added summary. Original failure output retained.
- Minimal fix: merge redundant title/summary into one platform TextView. Qualify normal directly as `السيارة ✓ طبيعية (تجريبي)` so the affirmative checkmark does not stand alone as a real vehicle assessment. Global `نسخة اختبار • بيانات تجريبية` and full gold stale warning remain visible. No data integration or new component/dependency.
- Build and Lint PASS: 0 errors, 15 warnings (previous14 plus unused home_no_alerts from this increment; no unrelated cleanup).
- API25/x86, 1024x600/160dpi, 1GB RAM/software GPU: existing instrumentation **4/4 PASS** in2.602s, including all-view/text fit and16dp gap.
- Actual UIAutomator-bound taps: three quick actions × Android Back/Return Home = **6/6 PASS**. Correct destination/selected tab and unavailable speed/navigation, idle media, stale warning, global TEST and qualified summary asserted after each return. Existing navigation/Settings/recreation/cold restart checks pass.
- Visual/semantic review of actual Home PNG and UI XML: no clipping, overlap or ellipsis. Summary occupies y350–378, full stale warning y386–438, vehicle button y446–502, bottom navigation begins y528. TEST badge is legible at top. Summary explicitly describes a test state, never a live vehicle assessment; no numeric vehicle readings or connectivity claim.
- All quick-action targets retain56px height; map280px wide, media/vehicle292px. Crash buffer empty; no app ANR detected.
- APK19,837 bytes, downloaded SHA256 verified: `a142b9a2182206dd30c057dfe70ca474713c4a17357b1ef874bb84faf5ef3c99`. Launch277ms (single emulator observation, not a benchmark).
- Durable [evidence](docs/test-evidence/p2-quiet-home-20260927/): before-failure output, final PNG/XML, lint XML, four-test output, six-flow JSON, summary, launch/memory/crash. Full artifact10933013785 expires2026-10-11.
- Local source guard, Python syntax and whitespace PASS. No physical T3/ARMv7/Test Station validation or Stable claim.

**Disposition: verification batch complete; commit/push and STOP. No next batch/P3.**

## 2026-09-27 — Quiet Home verification setup (historical; resolved above)

- Existing run36324136031 at d75e683 passed Build/Lint but failed 1/4 instrumentation tests: vehicle title clipped (Rect360,330–652,363) after adding the summary. Smoke stopped before screenshot/navigation; no pass claimed.
- Minimal correction: replace the redundant vehicle title plus summary with one summary line; explicitly qualify normal as `(تجريبي)` on that line, retaining the global TEST badge and full stale-source warning. No new component/dependency.
- Reuse existing platform TextView/layout and recorded REFERENCES decisions. Extend existing Home assertions to require the qualified summary on launch and all six quick-action returns.
- Full Build/Lint/API25/1024x600 rerun and visual review pending. Same verification batch only; STOP after checkpoint.

## 2026-09-27 — P2 Home quick-actions verification: PASS / STOP

Tested `cf062d6dfcee442755751e096acf6623fe29c8f8` in [run 36323597028](https://github.com/abo-sultann/DarbakOS/actions/runs/36323597028), job108632008811.
This batch adds targeted tests to the existing smoke script only. Application code remains the implementation from `b87ba33`; no app defect was found.

- **Build/Lint:** passed; 0 lint errors / 14 existing warnings (same categories as the preceding gate).
- **Existing instrumentation:** 4/4 passed in2.824s on API25/x86, 1024x600/160dpi, 1GB emulator RAM/software GPU. Includes RTL/fit/16dp gap, navigation, recreation and Home behavior.
- **New actual-touch verification:** each quick action tapped using UIAutomator bounds, exact section title/selected tab asserted, then Android Back and Return Home tested separately. **6/6 round trips passed**; Home unavailable/idle/stale labels and selected Home tab rechecked after every return.

| Button | Destination | Measured target | Android Back | Return Home |
|---|---|---|---|---|
| quick_map | الخريطة | 280x56px | PASS | PASS |
| quick_media | الوسائط | 292x56px | PASS | PASS |
| quick_vehicle | السيارة | 292x56px | PASS | PASS |

- **Screenshot:** final Home1024x600 inspected against the reviewed pre-test Home; pixel-identical. No clipping/overlap, spacing retained, all targets inside screen and at least56px high. Vehicle stale label is fully visible across two lines; no numeric stale reading is displayed live.
- **Regression smoke:** existing nav sections, Settings and cold restart still passed. Crash buffer empty; no app ANR detected during run.
- **APK:**19,557 bytes; downloaded SHA256 verified: `a3d330a43e291e3daa9fc2d904e4fb59a0b9c2547045967040d8535c8d5b3a14`.
- **Observations:** launch TotalTime399ms/WaitTime408ms; PSS8,776KB (~8.57MiB). Single emulator observations; no real-device or CPU-performance claim.
- **Evidence:** [p2-quick-actions-20260927](docs/test-evidence/p2-quick-actions-20260927/) retains Home PNG/XML,6-round-trip result JSON, summary, instrumentation, launch/memory/crash output. Full artifact10933750584 expires2026-10-11 and includes destination/return UI XML and logs.
- **Local checks:** source constraints, Python syntax and whitespace passed. No app/build dependency changes, hardware testing, new features or next phase.

**Disposition:** current verification batch complete; checkpoint and STOP. Not Stable/T3 acceptance.

## 2026-09-27 — Home quick-actions verification setup (historical)
- Main `d80f4fa6a247820826dd67ef59f5dbb75f313f45`; application implementation `b87ba3371d2d9c2aa3655c2ec7d8859f7519f24a`.
- Existing run https://github.com/abo-sultann/DarbakOS/actions/runs/36323265109 passed Build/Lint and the four existing tests, but did not tap the new quick actions.
- Actual Home PNG/UI XML inspected: no clipping/overlap; quick_map 280x56px, quick_media 292x56px, quick_vehicle 292x56px. Unavailable/idle/stale labels remain visible.
- Extend the existing emulator smoke only: tap each quick action, verify exact destination/selected tab, return with Android Back and Return Home, and recheck Home labels. New run pending; no app code changed.
- Scope remains verification-only. Finish evidence/checkpoint, then STOP.

## 2026-09-27 — P2 verification-only gate: PASS / STOP

Verified existing implementation `682d895c9e08ac6a4d3c9eacce69853db41cd6b2` using
[successful run 36308631278](https://github.com/abo-sultann/DarbakOS/actions/runs/36308631278), job `108590098596`.
At review, main was `17fe772379ac00e2848e5e71487eee36d21af72c`. Its only difference from the tested commit is
`02_NEXT_TASK.md`: `git diff --exit-code 682d895 HEAD -- app scripts .github gradle gradlew gradlew.bat build.gradle settings.gradle gradle.properties` passed.
The completed CI run was inspected, not rerun unnecessarily; downloaded evidence was checked against its exact code/hash.

| Required check | Verified result |
|---|---|
| Build/Lint | Successful workflow build of app/test APKs; lint XML: 0 errors, 14 warnings |
| Runtime | API25/default/x86, 1024x600, 160dpi; existing workflow's 1GB RAM/software GPU configuration |
| Existing instrumentation | 4/4 passed in 2.706s; launch/RTL, destinations/Back, recreation/Home, measured fit and 16dp gap |
| Navigation/cold restart | Existing UI-driven smoke passed, with crash/ANR checks |
| Home screenshot | Inspected actual emulator PNG: no clipped text, overlap or lost card spacing; vehicle text fits on two lines |
| Speed | Visible dash and disconnected test-source label; accessibility says speed unavailable |
| Navigation | Visible `الملاحة • غير متاحة` |
| Media | Visible `لا يوجد تشغيل • خامل` |
| Vehicle stale | Visible `آخر قراءة تجريبية قديمة • لا تعرض كقراءة حية`; no numeric vehicle reading shown as live |
| Test provenance | Global test-data badge visible; state text corroborated against captured Home UI XML |
| Crash/ANR | Empty crash buffer; smoke found no app ANR during the run |
| APK/hash | 18,793 bytes; SHA256 `f325038a164061b0b2e7c749108be8fcec7718d8a75d8533ba8a135df4730c8f` verified locally |
| Observations | Launch TotalTime408ms/WaitTime415ms; PSS8,610KB (8.41MiB), single emulator snapshots only |
| Changes needed | None to application/build/tests: no defect proven in this increment |

Lint warnings: 4 pinned test-library version suggestions, fixed landscape, 2 baseline-alignment suggestions,
nested weights, background overdraw, and 5 unused resources (including the 3 new generic state strings).
These do not block the requested gate; no cleanup or feature work was added. CPU performance and real T3/ARMv7/TestStation behavior are not established.

Durable evidence: [p2-verification-20260927](docs/test-evidence/p2-verification-20260927/), including Home PNG/XML,
explicit verification checks, instrumentation result, APK summary, launch/memory and empty crash output.
Full artifact: `10927543936`, named `DarbakOS-P1-TEST-682d895c9e08ac6a4d3c9eacce69853db41cd6b2` (existing workflow name retained), expires 2026-10-11.
Local source guard and documentation whitespace checks passed. No hardware/system work, next P2 increment or P3 was started.
**Disposition: checkpoint this verification result, then STOP. Not a Stable/T3 acceptance.**

## 2026-09-27 — P1 final acceptance at initial emulator stage

**PASS. Code:** `72fde4a84d57e52830cd509f2556ac867e2777e5`.
[Successful GitHub Actions run](https://github.com/abo-sultann/DarbakOS/actions/runs/36306964115).
This closes P1 only; it is not real-T3 or Stable acceptance.

| Check | Actual result |
|---|---|
| Build environment | Ubuntu24.04 GitHub runner; JDK17, Gradle8.9, AGP8.7.3; compileSdk35/build-tools34.0.0 |
| Runtime environment | API25/default/x86, 1024x600 pixels, 160dpi, 1024MB RAM/128MB heap, software GPU |
| Build | Debug app and instrumentation APKs produced; build/lint step passed |
| Lint | Zero errors; 11 warnings described below, no blanket lint suppression |
| Instrumentation | **4/4 passed**, 2.664s: launch/RTL/test state; all destinations+Back; recreation+Home; measured fit+16dp inter-card gap |
| UI smoke | UIAutomator-coordinate taps through Map/Media/Vehicle/Apps/Settings, screenshots and cold process restart to Home passed |
| Visual review | Six sections reviewed in preceding run; corrected Home rechecked in final run. Arabic text fits, speed left, nav RTL, final card gap visible |
| APK size | **18,221 bytes** (17.79KiB); no bundled native `.so` libraries |
| Launch | `am start -W`: Status ok; TotalTime **353ms**, WaitTime355ms (single launch, not benchmark) |
| Process memory | PSS **8,636KB**, about **8.43MiB**; one Activity/39 Views; snapshot after restart |
| CPU | **Not measured validly**: dumpsys window preceded the app process. Do not interpret as 0% |
| Crash/ANR | Empty crash buffer and no app ANR detected during this run |
| T3/ARMv7/Test Station | **Not tested**; all exact-unit safety/acceptance gates remain open |

APK SHA-256: `b392e732bf64140376aea61d08c61bf77927cc7ecd349e6f7062a69097e13722`.
Downloaded artifact was checked against this hash. Artifact id: `10927367313`, expires 2026-10-11.
Durable selected raw observations and Home/Settings screenshots: [docs/test-evidence/p1-20260927](docs/test-evidence/p1-20260927).
The Actions artifact contains both APKs, all six screenshots, UI XML, full logs and lint report.

Lint warnings are four newer test-library suggestions, deliberate fixed landscape, two baseline-alignment suggestions,
one nested-weight warning, background overdraw and two spare color tokens. Only the Play Store expired-target
check is disabled explicitly because this P1 build is an API25 sideload test. No functional/API errors remain.
No CPU/GPU/long-run/battery claim is inferred from these short tests.

**Follow-up:** one bounded P2 Home test-state increment in `02_NEXT_TASK.md`. No P2 work started in this batch.

## 2026-09-27 — P1 implementation checkpoint (historical)
- Base commit: `53c4b92d36ef9e3d4cebb7d5a11fed2fa2cfbc7d`; implementation commit: `6d784f318604c63354cdc5e5b367c2dcea0f60c0`.
- Work Linux x86_64, OpenJDK17.0.20; no Android SDK/adb/emulator/KVM available.
- PASS: `python3 scripts/check_baseline.py` (XML, API25, RTL/landscape, no permissions/background/native/runtime dependency drift).
- PASS: Python compilation for both scripts, `bash -n gradlew`, `git diff --check`.
- BLOCKED locally: `./gradlew --version` failed fetching Gradle8.9 (`java.net.SocketException: Network is unreachable`). SDK probe timed out at proxy; no local APK produced.
- NOT RUN yet: build/lint, API25 launch/navigation/recreation/1024x600 fit, runtime RAM/CPU and crash checks. GitHub Actions is prepared.
- No Test Station/T3 testing or hardware/system changes.
- Follow-up: finish P1 verification per `02_NEXT_TASK.md`; no P1 acceptance without measured evidence.

## P1 CI correction — same batch
- Implementation commit `6d784f318604c63354cdc5e5b367c2dcea0f60c0` reached GitHub main.
- [Run 36306645815](https://github.com/abo-sultann/DarbakOS/actions/runs/36306645815): source checks and official Gradle wrapper verification passed; SDK step failed with `sdkmanager: command not found` (exit127). Build/emulator steps were skipped; no APK/runtime pass.
- Correction: explicitly provision the SDK command-line tools with `android-actions/setup-android@v3` before sdkmanager; do not rely on preinstalled runner tools. Resolved by the subsequent passing run below.

## P1 visual correction — same batch
- [Run 36306734372](https://github.com/abo-sultann/DarbakOS/actions/runs/36306734372), commit `695c614a4f8535ab296f25173f2af6fcaed446e8`: build/lint, all 4 instrumentation tests, UI navigation and cold restart passed. Six screenshots inspected; no clipped text, but the RTL speed-card margin landed on the outer left edge and removed the intended inter-card gap.
- Corrected that margin from end to start on the explicitly RTL card; strengthened the existing 1024x600 test to assert the 16dp inter-card gap. Revalidation passed at final code `72fde4a` (see final acceptance above).
- Preliminary measurements: APK 18,217 bytes; launch TotalTime 296ms; process PSS 8,713KB (about 8.51MiB). These are x86 emulator observations, not T3 performance claims.
- Crash buffer empty; no app ANR detected. CPU snapshot covered an earlier boot interval with no app process, so app CPU is NOT MEASURED (not 0%).
- Lint had no errors; warnings concern pinned test-dependency versions, deliberate landscape, simple nested weights/baseline alignment/overdraw and spare color tokens. No runtime dependencies/native libraries in the APK.

## Test stages
1. Laptop/emulator initial validation
2. Darbak Test Station (Acer/BlissOS) where useful
3. Real Allwinner T3 commissioning/validation
4. Stable acceptance

For every test record: date, commit, environment, API/ABI/resolution, scenario, result, RAM/CPU where available, crash/ANR/log notes, screenshots/report reference, and follow-up.
