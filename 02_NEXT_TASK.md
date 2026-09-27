# Next Task — P2 Home test states (one small batch)

## Start point
P1 passed initial API25 emulator validation at `72fde4a`; see `TEST_RESULTS.md`.
Use the existing platform-View shell and design tokens. Do not recreate the project or reopen the master plan.

## Goal
Complete one bounded part of the approved P2 phase: reusable Home card presentation for clearly labeled test states.
Keep the existing navigation and Arabic 1024x600 layout.

## Required output
- Extend the existing Home card primitives to render a small explicit test fixture set: unavailable/idle and stale data.
- Keep speed unavailable unless visibly marked as simulated; stale values must never appear live.
- Preserve test labeling, physical left speed position, RTL navigation, large touch targets and owner's Launcher palette.
- Review REFERENCES and relevant existing owner/upstream components before adding anything; record any reuse.
- Add/extend only tests needed for the changed states and their navigation/layout behavior.
- Build/lint and run affected API25/1024x600 tests; inspect changed screens and record observations.

## Constraints
No real sensors, OsmAnd, Trip, media playback, hardware/firmware/root/MCU/70mai integration, new services or heavy UI dependencies.
Do not enter P3 Core/Guardian in this batch. Do not use the real T3 as the first test device.

## Finish definition
One test-state/card increment is implemented, tested and visually checked. Commit/push it, then update
`01_CURRENT_STATUS.md`, this next single task, `CHANGELOG.md` and `TEST_RESULTS.md` before doing more.

## Build environment note
Current Work has no Android SDK/emulator and build downloads are unavailable locally. The checked-in
GitHub Actions workflow provisions tools explicitly and has a proven successful API25 run.
Use `docs/P1_BUILD_AND_TEST.md`; inspect the run and artifact for the exact new commit.
If direct git push lacks credentials, use the authorized GitHub connector for an atomic non-force commit/ref update,
then synchronize the local checkout. Never mark tests passed before reading their actual evidence.
