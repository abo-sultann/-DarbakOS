# P2 glanceable navigation-card verification — COMPLETE / STOP

## Closure — 2026-09-27
Verified b054d7d using run36325924001; current app/build/test inputs match. Build/Lint,4/4 API25 tests and6/6 quick-action returns pass. Actual Home screenshot and eight captured Home UI states confirm test-only maneuver/ETA/distance, visible unavailable state and preserved prior states. No defect or application change needed. Evidence/results are recorded in TEST_RESULTS.md.

STOP. This file records closure, not a new assignment. No P3 or additional batch.

## Original start point
All prior P2 Home gates passed. Main now contains one bounded Home-only increment: the navigation card uses explicit TEST-only next-turn and ETA/distance examples to validate the intended glanceable hierarchy. No navigation engine, GPS, OsmAnd or service was added.

## Goal
Verify only the navigation-card presentation and regressions.

## Required verification
- Build/Lint and existing API25/1024x600 tests.
- Inspect Home screenshot at 1024x600 for clipping/overlap and confirm the navigation card hierarchy is readable at a glance.
- Confirm both maneuver and ETA/distance are explicitly marked test data and cannot be mistaken for live navigation.
- Confirm navigation unavailable state remains visible.
- Regress quick actions, Back/Home and prior unavailable/idle/stale/test-only vehicle states.
- If this increment causes a proven defect, fix only that defect and rerun affected checks.
- Record evidence/results; update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md.
- Commit + push, then STOP.

## Constraints
No OsmAnd/GPS/Trip/navigation engine, services, sensors, playback, Vehicle integration, new dependencies, P3, firmware/root/MCU/70mai or T3 work.

## Finish definition
The glanceable TEST navigation card is visually/semantically verified at API25/1024x600 and checkpointed, or a precise blocker is documented.
