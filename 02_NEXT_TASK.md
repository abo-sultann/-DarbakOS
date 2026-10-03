# Next Task — P9 Modern Head Unit Readiness

Updated: 2026-10-03.

## Target decision
The failed Allwinner T3 unit is retired from in-car production use. Darbak OS now targets the incoming modern Android head unit. T3/API25/ARMv7/1024x600 remains a legacy compatibility baseline only and must not constrain new development.

## Objective
Continue hardware-independent development while the replacement head unit is being selected/acquired, then commission the exact new unit non-destructively.

## Software-first slice
- Preserve all accepted P4-P8 behavior and truthful-state rules.
- Keep platform-specific/OEM behavior behind adapters; do not hard-code T3/MCU assumptions into product logic.
- Make layouts responsive to the actual future display rather than assuming 1024x600.
- Keep current API25 CI as a regression floor until the new unit establishes the production min/target requirements.
- Do not add heavy dependencies merely because the future hardware is stronger.
- Prepare baseline tooling so the new unit can be identified before any OEM/boot/system integration.

## When the new screen arrives
1. Capture Android/API, build fingerprint, SoC/ABI, RAM/storage, display/density, USB/GPS/Bluetooth/Wi-Fi and read-only OEM/CANBUS identifiers.
2. Run bounded Darbak APK smoke tests.
3. Verify ESP32/Darbak TPMS connectivity, GPS, Media and OsmAnd.
4. Define the new unit's Golden Backup/recovery path where applicable.
5. Only after those gates, design autostart/OEM/CANBUS integration for the exact new hardware.

## Hard stop
No firmware/MCU/kernel flashing, destructive root, OEM hiding, boot replacement or system-app removal before the exact replacement unit is known and its recovery path is verified.

## Exit
Modern-head-unit-ready software baseline + exact replacement-device commissioning evidence. Then advance to P10 integration for that hardware.
