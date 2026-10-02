# Next Task — P9 Exact T3 Baseline + Golden Backup Gate

Updated: 2026-10-02.

## Objective
Start real-device commissioning safely. Identify the exact Allwinner T3 head unit and establish a recoverable Golden Backup gate before any deep system integration.

## First slice
- Collect a non-destructive exact-device baseline only: Android/API, build fingerprint, board/product/device/model, CPU ABI, display, storage, package list and relevant read-only system properties.
- Record exact MCU/system identifiers that Android exposes read-only; do not flash or change them.
- Define the Golden Backup manifest: backup identity, files/images included, SHA-256 hashes, acquisition method and verified recovery path.
- Do not mark Recovery eligible until both backup hashes and an independently verified recovery procedure exist.
- Preserve the current stable APK and all accepted P4-P8 evidence.

## Hard stop
No firmware/MCU/kernel flashing. No destructive root. No OEM hiding, boot replacement, system-app removal or autostart integration before this gate passes.

## Exit
Exact-device baseline recorded + Golden Backup/recovery path verified and hashed. Only then proceed to deeper P9/P10 T3 integration.
