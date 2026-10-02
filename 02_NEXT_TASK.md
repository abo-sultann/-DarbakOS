# Next Task — P8 Safe Update Package Inspection

Updated: 2026-10-02.

## Objective
Add a read-only local update-package inspection boundary without installing, flashing or modifying the system.

## Scope
- Define a lightweight update package metadata/result contract.
- Inspect only a user-selected/local Darbak APK candidate: file presence, size, SHA-256 and package/version metadata where Android can read it.
- Report compatible / incompatible / unreadable truthfully; never infer safety from filename.
- Keep install/update execution disabled in this slice.
- Surface the inspection result only inside hidden Admin.
- Add focused API25 tests using deterministic fixtures/contracts.

## Constraints
No firmware/MCU/kernel/root, PackageInstaller execution, silent install, OEM hiding, boot changes or recovery actions. Preserve P4-P7 and accepted P8 Admin diagnostics. No Full Regression or Guardian suites.

## Exit
Pass focused API25 inspection tests and record evidence. Then choose the smallest remaining P8 recovery-state slice. Real Golden Backup/recovery verification stays P9.
