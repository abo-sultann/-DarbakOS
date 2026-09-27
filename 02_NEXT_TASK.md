# Next Task — P2 verification gate

## Start point
P1 is complete. The first P2 Home test-state increment is now implemented on main.

## Goal
Verify the existing P2 increment only. Do not expand scope.

## Required verification
- Build/lint on the proven API25 workflow.
- Run the existing API25/1024x600 navigation/layout tests.
- Inspect Home screenshot for clipping/overlap/card spacing.
- Confirm the Home visibly distinguishes: speed unavailable, navigation unavailable, media idle, vehicle stale.
- Confirm no stale vehicle value is presented as live.
- If an observed defect is caused by this increment, fix only that defect and rerun affected checks.
- Record exact run/commit/evidence in TEST_RESULTS.md and update CURRENT_STATUS/CHANGELOG.
- Commit/push the completed checkpoint, then STOP. Do not begin another P2 increment or P3.

## Constraints
No sensors, OsmAnd, Trip, playback, hardware/firmware/root/MCU/70mai, new services, heavy dependencies, or T3 testing.

## Finish definition
The P2 test-state increment is build/test/screenshot verified and checkpointed in GitHub. If verification cannot run, document the exact blocker and leave it unaccepted.
