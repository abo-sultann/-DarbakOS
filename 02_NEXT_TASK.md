# Next Task — P7 User Settings Foundation

Updated: 2026-10-02.

## Objective
Implement the smallest useful user-facing Settings slice after the accepted P7 Apps gate.

## Scope
- Replace the generic Settings placeholder with a dedicated lightweight Settings surface.
- Expose only safe user-facing settings that Darbak can truthfully own on API25.
- Keep technical Admin, firmware/MCU/root/recovery and OEM controls out of this surface.
- Persist only settings actually implemented; no fake toggles.
- Preserve Arabic RTL, 1024x600 and ~1GB constraints.
- Add focused API25 tests for Settings persistence/UI behavior only.

## Constraints
Preserve P4 OsmAnd/Trip, P5 Media, P6 Vehicle and the accepted P7 Apps slice. No Full Regression or Guardian suites. Do not start Standby or alerts in this batch.

## Deferred
Standby and actionable alerts remain later P7 slices. Physical ARMv7/Test Station/T3 acceptance remains P9/P10.
