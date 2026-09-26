# Next Task — P1 Laptop Baseline

## Goal
Create the smallest buildable Darbak OS Android baseline for API 25 and 1024x600 landscape, suitable for laptop/emulator testing before any real T3 experiment.

## Required output
- Minimal Android project/shell compatible with API25 and ARMv7 target constraints.
- Arabic RTL-first 1024x600 landscape baseline.
- Lightweight Darbak design-system primitives sufficient for the shell.
- Home shell with placeholder/test state only; no heavy integrations yet.
- Build instructions reproducible from a clean checkout.
- Basic tests/checks for launch, navigation shell, RTL, 1024x600 fit and obvious crashes.
- Record build/runtime observations and resource measurements available in the test environment.

## Constraints
Do not add OsmAnd, vehicle hardware, firmware, root, MCU, 70mai or production Trip integration in this batch.
Do not use modern dependencies that force minSdk >25.
Avoid heavy UI frameworks/dependencies unless measured and justified.
Reuse suitable reference code/concepts only after recording them in REFERENCES.md.

## Finish definition
Build succeeds, shell launches in the initial test environment, results are written to TEST_RESULTS.md, changes are committed/pushed, CURRENT_STATUS and NEXT_TASK are updated before starting another phase.
