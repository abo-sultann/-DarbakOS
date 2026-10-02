# Next Task — P7 Actionable Alerts Foundation

Updated: 2026-10-02.

## Objective
Finish the bounded P7 foundation with quiet-by-default, actionable alerts driven only by existing truthful Darbak state.

## Scope
- Define a lightweight alert contract/state for actionable abnormal conditions.
- Normal/unavailable-without-action states remain quiet; do not turn ordinary missing hardware into alarm noise.
- Surface alerts in the existing Home experience without blocking Map/Media/Vehicle/Apps/Settings/Standby.
- Use existing state contracts only; no new TPMS/OBD/fridge hardware integration in this batch.
- No fake values, inferred danger or stale data presented as live.
- Add focused API25 tests for quiet normal state and one deterministic actionable abnormal fixture.

## Constraints
Preserve P4 OsmAnd/Trip, P5 Media, P6 Vehicle and accepted P7 Apps/Settings/Standby. No Full Regression or Guardian suites.

## Exit
If the focused alert gate passes, close the P7 foundation software scope and advance to P8 Update/Admin/Recovery foundation. Apps Recent/Favorites may remain a later enhancement unless required by acceptance.
