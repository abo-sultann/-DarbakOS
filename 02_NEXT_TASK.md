# Next Task — P8 Update/Admin/Recovery Foundation

Updated: 2026-10-02.

## Objective
Start the smallest safe software-only P8 foundation after accepted P7 closure.

## Scope
- Define a hidden/technical Admin boundary separate from user Settings.
- Add truthful read-only app/build/device diagnostics needed for later update and recovery work.
- Define update/recovery states and contracts without performing firmware, MCU, kernel, root or destructive actions.
- Keep recovery guidance explicit about what is unavailable until P9 hardware commissioning and Golden Backup verification.
- Add focused API25 tests only for the new Admin/diagnostic boundary.

## Constraints
Preserve P4 Trip/OsmAnd, P5 Media, P6 Vehicle and all accepted P7 behavior. No Full Regression or Guardian suites. No firmware/MCU flashing, kernel changes, destructive root, OEM hiding, boot integration or real T3 recovery operations.

## Exit
Pass the focused API25 P8 Admin/diagnostic gate, record evidence, then choose the next bounded P8 update/recovery slice. Real T3 Golden Backup/recovery verification remains P9.
