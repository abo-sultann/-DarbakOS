# Changelog

## 2026-09-29 — Guardian passive Supervisor verification closed / STOP
- Added8 tests for the full Snapshot -> Assessment -> Plan chain:6144 combination/level cases, null/cold/extreme inputs, nonmutation, repeated evaluation and retained results after update/reset. All57 prior tests preserved.
- Two test-only callers verified20000 composed evaluations during atomic health updates/reset, with exact internal state/revision/assessment/plan consistency and joined threads. No production worker added.
- Codeee62972 passed Build/Lint,65/65 API25 tests and6/6 navigation returns/Cold Restart/no-autoplay; CI captured2026-09-28. No application defect/fix needed.
- Home/Apps/restart images pixel-identical; APK +412bytes/PSS -48KB, no app crash/ANR; separate observations are not causal benchmarks.
- Saved evidence and updated state/gate. Caller-driven passive evaluation only; no watchdog, heartbeat, timers, logging or actual recovery.

## 2026-09-28 — Guardian recovery-policy verification closed / STOP
- Added7 tests covering6144 combination/level cases, conservative UNKNOWN/DEGRADED limits, exact FAILED ladder, negative/extreme levels, targets and immutable plan/input retention. All50 prior tests preserved.
- Code987884c passed Build/Lint,57/57 API25 tests and6/6 navigation returns/Cold Restart/no-autoplay. No application defect/fix needed.
- Home/Apps/restart images pixel-identical; APK +1,356bytes/PSS -155KB, no app crash/ANR; observations are not causal benchmarks.
- Saved evidence and updated state/gate. Recommendations only; no actual recovery, watchdog, logging or Supervisor.

## 2026-09-28 — Guardian assessment verification closed / STOP
- Added7 tests for all1024 classifications, exclusive membership/counts, null/cold/all-healthy, immutable retention after updates/reset and repeated frozen-input assessments. All43 prior tests retained.
- Codef1d53a6 passed Build/Lint,50/50 API25 tests and6/6 navigation returns/Cold Restart/no-autoplay. No application defect/fix.
- Home/Apps/restart images pixel-identical; APK +1,296bytes/PSS +92KB, no app crash/ANR; observations are not causal benchmarks.
- Updated evidence/results/state and closed gate. No watchdog/logging/recovery/background work.

## 2026-09-28 — Guardian snapshot verification closed / STOP
- Reproduced an inconsistent snapshot (CORE FAILED, overall HEALTHY) under concurrent updates; fixed capture to hold the existing registry monitor through component reads/aggregation. No production thread/background behavior added.
- Added7 snapshot tests covering null/cold, all1024 combinations, exact identities, retained values after updates/reset, repeated capture/recovery and20000 concurrent captures. All36 prior tests retained.
- Code3709e35 passed Build/Lint,43/43 API25 tests and6/6 navigation returns/Cold Restart/no-autoplay. Home/Apps/restart images pixel-identical.
- APK +340bytes/PSS +97KB, no app crash/ANR; resource observations are not causal benchmarks. Saved race proof and verification evidence, updated state and closed gate. No watchdog/logging.

## 2026-09-28 — Guardian aggregate-policy verification closed / STOP
- Added7 tests covering all1024 combinations, precedence, every sole problem source, null/cold boundaries, recovery/reset and unchanged snapshot identities/revisions. All29 prior tests retained.
- Codee42f2d6 passed Build/Lint,36/36 API25 tests and6/6 navigation returns/Cold Restart/no-autoplay. No application defect/fix.
- Home/Apps/restart images pixel-identical; APK +236bytes/PSS -26KB, no crash/ANR; observations are not causal benchmarks.
- Updated evidence/results/state and closed gate; no watchdog/logging/background work.

## 2026-09-28 — Guardian component-registry verification closed / STOP
- Added7 focused tests covering all5 components/60 distinct transitions, isolation, revisions, same/null identity, null component, retained state, repeated reset and FAILED-to-HEALTHY recovery. All22 prior tests retained.
- Code7ef7037 passed Build/Lint,29/29 API25 tests and6/6 navigation returns/Cold Restart/no-autoplay. No application defect/fix.
- Home/Apps/restart screenshots pixel-identical; APK +912bytes/PSS +79KB, no crash/ANR; observations are not causal benchmarks.
- Updated evidence/results/state and closed gate; no watchdog/logging/callbacks/background work.

## 2026-09-28 — Guardian health-state verification closed / STOP
- Added5 tests covering all4 states/all12 distinct transitions, revision increments, immutable retention, same/null identity and FAILED-to-HEALTHY recovery. Retained all17 prior tests.
- Codee994833 passed Build/Lint,22/22 API25 tests and6/6 navigation returns/Cold Restart/no-autoplay. No application defect/fix.
- Final P2 preserved; documented initial Home focus highlight and identical Apps/restart images. APK +536bytes/PSS -238KB, no crash/ANR; observations are not causal benchmarks.
- Updated evidence/results/state and closed gate; no watchdog/logging/background work.

## 2026-09-28 — Cold-reset publication verification closed / STOP
- Added3 focused reset/listener tests for exact-once synchronous delivery, snapshot identity, removal and repeatable cold states; all14 prior tests retained.
- Codecdc64b3 passed Build/Lint,17/17 API25 tests and6/6 quick-action returns/Cold Restart/no-autoplay. No application defect/fix needed.
- Home/Apps/restart images pixel-identical; APK -16 bytes/PSS -37KB; no observed crash/ANR. Resource observations are not causal benchmarks.
- Updated state/results/evidence and closed the gate. No Guardian/logging or subsequent work.

## 2026-09-28 — Core publish/subscription verification closed / STOP
- Added6 focused API25 tests for immutable revision/publish/listener identity, synchronous delivery, duplicate prevention, removal, null and cold-reset behavior; retained all8 prior tests.
- Code31ce9bc passed Build/Lint,14 tests and6 quick-action returns/Cold Restart/no-autoplay. No application defect/fix needed.
- UI images pixel-identical; recorded APK +712 bytes/PSS +173KB and single-launch observations with limitations, no crash/ANR.
- Updated evidence/state/results/gate; no Guardian/logging or subsequent P3 work.

## 2026-09-27 — P3 Core State verification closed / STOP
- Added3 focused API25 tests for truthful coldBoot, singleton snapshot/reset retention and fresh Activity versus recreation semantics; retained all5 UI tests.
- Code6bcc623 passed Build/Lint,8 tests,6 quick-action returns and Cold Restart/no-autoplay. No application defect/fix needed.
- Home/Apps/restart images pixel-identical to P2. Recorded APK +1,682 bytes, PSS +117KB and launch snapshots with benchmark limitations; no crash/ANR.
- Saved evidence and updated state/results/gate. No Guardian, logging or further P3 work.

## 2026-09-27 — Final-product UI verification closed / STOP
- Accepted current Darbak UI as final-product interface with truthful disconnected states; historical TEST-copy expectations superseded.
- Corrected enabled Apps actions without backends: disabled/dimmed them while preserving intended labels/RTL layout.
- Updated exact state tests and rejected temporary visible/accessibility wording; added Apps fit/RTL/unavailability test without dropping prior behavior checks.
- Code27e0e79 passed Build/Lint,5 API25 tests,6 quick-action returns and Cold Restart/no-autoplay; Home/Apps images reviewed.
- Saved evidence and updated state/results/gate; no P3/backend/next batch.

## 2026-09-27 — Stopped Media verification closed / STOP
- Corrected outdated idle-text smoke expectation for stopped TEST preview; application unchanged.
- Added exact media/navigation-test regressions and Cold Restart/settled Home checks with no app service/MediaSession assertions and raw diagnostics.
- Codeabc0b3d passed Build/Lint,4 API25 tests and6 quick-action returns; screenshots reviewed at1024x600 without clipping/overlap.
- Saved evidence and updated state/results/gate. No next batch or P3.

## 2026-09-27 — Navigation-card verification closed / STOP
- Verified existing b054d7d Build/Lint,4 API25 tests and6 quick-action round trips from run36325924001; no application change needed.
- Reviewed Home1024x600: no clipping/overlap; maneuver and ETA/distance each explicitly test-only, navigation unavailable visible.
- Rechecked all labels in eight captured Home states, retained evidence and updated status/results/gate. No next batch/P3.

## 2026-09-27 — Quiet Home verification closed / STOP
- Fixed proven clipping introduced by the new vehicle summary by merging the redundant title/summary.
- Labeled normal explicitly `(تجريبي)` beside the status, preserving global TEST and full stale warning.
- Extended existing Home assertions; code9ac0b10 passed Build/Lint,4 API25 tests and6 quick-action returns. Actual screenshot reviewed at1024x600.
- Saved failure/final evidence and updated state/results/gate. No next batch or P3.

## 2026-09-27 — Home quick-actions verification closed / STOP
- Added targeted emulator smoke checks for three Home quick actions, correct destination/selected tab, Android Back and Return Home, with label/56px target assertions.
- Code `cf062d6` passed Build/Lint,4 existing API25 tests and6 actual-tap round trips at1024x600.
- Reviewed final Home image; no clipping/overlap or app defect found. App code unchanged.
- Preserved evidence and updated state/gate/results. No additional P2 increment or P3 started.

## 2026-09-27 — P2 verification-only checkpoint / STOP
- Verified the existing Home unavailable/idle/stale labels from code `682d895`; main `17fe772` has identical application/build/test inputs.
- Reviewed successful Build/Lint, 4 API25/1024x600 tests, navigation/restart smoke and the actual Home screenshot.
- Confirmed no clipping/overlap and no stale vehicle value presented as live; no application fix was necessary.
- Preserved evidence and updated current status, gate closure and test results. No subsequent task or phase started.

## 2026-09-27 — P1 closed / initial emulator validation passed
- Final code `72fde4a` passed build/lint, four Android tests, UI navigation and cold restart on API25/x86 at 1024x600.
- Fixed RTL dashboard spacing after screenshot review and verified the exact 16dp gap.
- Recorded final APK hash/size, launch/PSS snapshots, CPU measurement limitation and clean crash/ANR result.
- Preserved selected evidence/screenshots, updated official status and defined only the next small P2 task.
- No T3/firmware/system work; no Stable declaration; no next batch started.

## 2026-09-27 — P1 implementation checkpoint (verification pending)
- Added separate Arabic RTL/landscape TEST shell with Home and placeholder navigation.
- Reused owner Launcher palette; reviewed Launcher.v2, TestStation and Dashline with license/compatibility notes.
- Added pinned Gradle/AGP, four Android instrumentation tests, emulator smoke/evidence workflow and build instructions.
- Local source checks passed; APK/runtime acceptance awaits remote CI because local Android tools/downloads are unavailable.

## P1 visual verification
- API25 build and tests passed; screenshot review found reversed relative spacing on the speed card. Corrected RTL margin and added a measured 16dp gap assertion to the existing fit test.

## P1 CI environment fix
- First remote run exposed missing sdkmanager on Ubuntu24.04; explicitly provision Android SDK tools. Still within the original P1 verification batch.

## Planning baseline v1.0
- Established Darbak OS dedicated project repository.
- Added Work master plan and continuation/checkpoint workflow.
- Locked target hardware/API constraints.
- Locked firmware/MCU/Golden Backup safety rules.
- Added laptop-first testing path.
- Added reuse-first reference policy.
