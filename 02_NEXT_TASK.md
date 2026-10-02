# Next Task — P7 Apps / Settings / Standby / Alerts Foundation

Updated: 2026-10-02.

## Objective
Start the next bounded software phase after P6 closure, without reopening accepted P4/P5/P6 behavior.

## Approved first scope
- Define the lightweight P7 boundaries for Apps, user Settings, Standby and actionable alerts.
- Keep Apps focused on installed-app discovery/launch and management entry points; do not build another launcher framework.
- Keep Settings user-facing and simple; technical Admin remains separate/deferred.
- Standby must be lightweight, calm and must not stop Position/Trip recording.
- Alerts are for actionable abnormal states; normal state remains quiet.
- Preserve Arabic RTL, 1024x600, API25, ~1GB constraints and truthful unavailable states.
- Add only focused API25 tests for the first P7 slice selected during implementation.

## Constraints
No firmware/MCU/kernel/root work. No heavy dependency or extra process without proven need. No Full Regression or Guardian suites unless a proven defect requires them. Preserve P4 OsmAnd/Trip, P5 Media and P6 Vehicle contracts.

## Deferred hardware acceptance
Physical ARMv7/Test Station/T3 behavior, OEM hiding/autostart/boot and hardware-source integration remain P9/P10 work.
