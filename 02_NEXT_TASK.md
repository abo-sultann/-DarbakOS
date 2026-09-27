# Next Task — P2 quiet Home summary verification

## Start point
The prior P2 quick-actions gate passed. Main now contains one further bounded Home-only increment: a quiet normal vehicle summary ("السيارة ✓ طبيعية") alongside the explicit stale TEST-source warning. This is presentation/test data only and must not imply real vehicle connectivity.

## Goal
Verify the Home presentation and semantic clarity only.

## Required verification
- Build/Lint and existing API25/1024x600 tests.
- Inspect Home at 1024x600 for clipping/overlap after the added normal summary.
- Confirm the normal summary cannot reasonably be mistaken for a real live vehicle reading because the global TEST badge and stale-source warning remain visible.
- Confirm quick actions, Back/Home and unavailable/idle/stale states still regress cleanly.
- If a proven defect is caused by this increment, fix only that defect and rerun affected checks.
- Record evidence/results; update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md.
- Commit + push, then STOP.

## Constraints
No real vehicle data, sensors, OsmAnd, Trip, media playback, services, dependencies, P3, firmware/root/MCU/70mai or T3 work.

## Finish definition
The quiet Home summary is visually and semantically verified at API25/1024x600 and checkpointed, or a precise blocker is documented.
