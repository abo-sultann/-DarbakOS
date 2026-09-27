# Next Task — P2 stopped-media-card verification

## Start point
Prior P2 Home/navigation gates passed. Main now contains one bounded Home-only increment: the Media card shows a static TEST track/title and saved position in an explicitly stopped state. No playback engine, MediaSession, storage scan or service was added.

## Goal
Verify only the Media-card presentation, no-autoplay semantics and regressions.

## Required verification
- Build/Lint and existing API25/1024x600 tests.
- Inspect Home screenshot for clipping/overlap after the Media-card change.
- Confirm track/position are explicitly TEST data and state is visibly stopped; nothing may imply audio is currently playing.
- Cold restart must still land on Home and must not create any playback/service behavior.
- Regress quick actions, Back/Home, navigation TEST card, unavailable speed/navigation and vehicle test/stale states.
- If this increment causes a proven defect, fix only that defect and rerun affected checks.
- Record evidence/results; update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md.
- Commit + push, then STOP.

## Constraints
No playback, MediaSession, audio focus, storage scan, OsmAnd/GPS/Trip, Vehicle services, new dependencies, P3, firmware/root/MCU/70mai or T3 work.

## Finish definition
The stopped TEST Media card and no-autoplay semantics are visually/behaviorally verified at API25/1024x600 and checkpointed, or a precise blocker is documented.
