# Next Task — finish P1 verification

## Goal
Inspect the **P1 Android baseline** GitHub Actions run for the P1 implementation commit. Finish the same P1 batch: build/lint, API25 emulator tests, screenshots and observations. Do not start P2 while this gate is pending.

## Required output
- Build debug/instrumentation APKs and pass lint.
- Pass four Android instrumentation tests on API25/x86 at 1024x600/160dpi.
- Pass navigation/cold-restart smoke and visually inspect generated screenshots.
- Record APK size/hash, launch/RAM/CPU observations, crash/ANR status and run URL.
- Fix only observed failures, rerun affected checks, and push the checkpoint.

## Environment constraint
Current Work has JDK17 but no SDK/adb/emulator/KVM. SDK download timed out at proxy; Gradle download returned Network is unreachable. Use the checked-in GitHub Actions workflow. If Actions cannot run, preserve the precise blocker and keep P1 unaccepted. Reproduce with `docs/P1_BUILD_AND_TEST.md`.

## Constraints
Do not add OsmAnd, vehicle hardware, firmware, root, MCU, 70mai or production Trip integration in this batch.
Do not use modern dependencies that force minSdk >25.
Avoid heavy UI frameworks/dependencies unless measured and justified.
Reuse suitable reference code/concepts only after recording them in REFERENCES.md.

## Finish definition
Build succeeds, shell launches in the initial test environment, results are written to TEST_RESULTS.md, changes are committed/pushed, CURRENT_STATUS and NEXT_TASK are updated before starting another phase.
