# Next Task — P6 Vehicle Data Foundation

Updated: 2026-10-02.

## Objective
Implement the P6 software boundary without waiting for unresolved physical TPMS/OBD/CAN decoding.

## Approved scope
- Immutable vehicle values/snapshot with source provenance and observed timestamp/freshness.
- Optional source-adapter interface for TPMS ESP32/CC1101, OBD/CAN, fridge and later sensors.
- Truthful unavailable/stale handling; stale data must never be presented as live.
- Vehicle UI consumes only the unified snapshot contract.
- Focused API25 tests for unavailable, fresh and stale transitions and source attribution.

## Non-blockers
Toyota TPMS decoding, OBD/CAN discovery, fridge protocol and physical sensor connectivity are independent hardware integrations. Their absence does not block P6 software closure.

## Constraints
No fabricated vehicle values. No MCU/kernel/firmware work. No heavy dependency or extra process. Preserve P3/P4/P5 behavior. Physical T3 acceptance remains P9/P10.
