# Next Task — P2 Home quick-actions verification gate

## Start point
Previous P2 state-label gate passed. A second bounded P2 increment is now on main: lightweight Home quick actions route to the existing Map, Media and Vehicle placeholder sections. No new service or integration was added.

## Goal
Verify this increment only; do not expand scope.

## Required verification
- Build/Lint using the proven API25 workflow.
- Run existing API25/1024x600 tests and smoke navigation.
- Verify quick_map -> Map, quick_media -> Media, quick_vehicle -> Vehicle, and Back -> Home.
- Inspect Home screenshot at 1024x600 for clipping, overlap, card spacing and touch-target fit after the added buttons.
- Ensure existing unavailable/idle/stale labels remain visible and no stale vehicle value appears live.
- If this increment causes a proven defect, fix only that defect and rerun affected checks.
- Record evidence in TEST_RESULTS.md and update CURRENT_STATUS.md/CHANGELOG.md.
- Commit + push the completed checkpoint, then STOP.

## Constraints
No OsmAnd, sensors, Trip, playback, Vehicle services, firmware/root/MCU/70mai, new dependencies, P3, or T3 testing.

## Finish definition
Quick actions and 1024x600 Home fit are verified and checkpointed. If verification cannot run, record the exact blocker and leave this increment unaccepted.
