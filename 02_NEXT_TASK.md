# Next Task — P7 Standby Foundation

Updated: 2026-10-02.

## Objective
Implement a lightweight Darbak Standby surface without interrupting Position/Trip recording.

## Scope
- Add an explicit user-triggered Standby mode/surface suitable for the 1024x600 car screen.
- Keep the surface calm and minimal; avoid animations, background loops and heavy dependencies.
- Standby is a Darbak UI state, not device power-off and not Android sleep/root control.
- Entering/leaving Standby must preserve the continuous TripRuntimeService contract.
- Long-press or another child-safe deliberate action is required for any sensitive exit/power-adjacent behavior; do not add a screen-off button.
- Add focused API25 tests for enter/exit and TripRuntime continuity only.

## Constraints
Preserve P4 OsmAnd/Trip, P5 Media, P6 Vehicle and accepted P7 Apps/Settings. No Full Regression or Guardian suites. Do not start alerts in this batch.

## Deferred
Actionable alerts remain the final bounded P7 slice. Physical screen power/ACC/vendor MCU behavior remains P9/P10.
