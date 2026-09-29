# Next Task — P3 FINAL CLOSURE GATE

## Goal
Close P3 now. Do not extend Guardian architecture further.

## Execute in one bounded pass
1. Review all production changes since the last closed Monitor Session checkpoint through GuardianDiagnosticRecord.
2. Run Build/Lint and the already-focused diagnostics test only if needed for source drift.
3. Run exactly ONE bounded API25/1024x600 regression covering P2 UI/Core/Guardian integration. Reuse unchanged exhaustive 1024/6144/concurrency proofs by fingerprint; do not regenerate them.
4. Fix only proven defects, with focused retest first. Do not add features.
5. Verify final UI remains unchanged/truthful/no-autoplay and production still has no unintended Service/Receiver/permission/native/dependency/background/recovery execution.
6. Update 01_CURRENT_STATUS.md, TEST_RESULTS.md, CHANGELOG.md to mark P3 CLOSED if PASS.
7. Replace this file with a P3 COMPLETE / P4 READY checkpoint. Commit + Push; STOP.

## P3 closure boundary
P3 delivers Core State + passive Guardian health/assessment/recommendation + passive monitoring/session + compact diagnostics. Automatic watchdog, actual recovery executor and persistent support export are deferred to the later integration/recovery phase; they are not blockers for P3 closure.

## Forbidden in this gate
No new Guardian features. No Watchdog/Service/automatic emitters. No OsmAnd/GPS/Trip, Media, Vehicle, firmware/root/MCU/70mai or T3 work.

## Finish
One regression gate only. If green, P3 is closed and P4 may begin.
