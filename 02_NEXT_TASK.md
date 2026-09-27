# Next Task — P2 production-facing shell verification

## Start point
P2 is now treated as the real Darbak OS UI, not a disposable prototype. Main removes user-facing TEST/preview/fake-data wording from Home and Apps. Sources that are not yet connected show honest final states such as unavailable, stopped, or no active route. The Apps section contains the intended Recent, Favorites and App Management actions, but their backend behavior is not implemented yet.

## Goal
Verify the production-facing P2 shell and Apps presentation without expanding backend scope.

## Required verification
- Build/Lint and all existing API25/1024x600 tests.
- Inspect Home and Apps screenshots at 1024x600 for clipping/overlap, RTL readability and touch-target fit.
- Confirm no user-facing wording says TEST, experimental, preview, or presents fabricated live values.
- Confirm disconnected features use honest final-state copy: speed unavailable, no active navigation route, media stopped/no selected track, vehicle data unavailable.
- Confirm Apps actions appear only in Apps and labels are Recent, Favorites, App Management.
- Regress quick actions, Back/Home, Cold Restart and no-autoplay behavior.
- Update tests that legitimately encoded the old temporary wording; do not weaken behavioral assertions.
- Fix only proven defects from this conversion, rerun affected checks, record evidence, update CURRENT_STATUS.md / TEST_RESULTS.md / CHANGELOG.md.
- Commit + push, then STOP.

## Constraints
Do not implement PackageManager discovery/install/uninstall, OsmAnd/GPS/Trip, playback, Vehicle sources, services, new dependencies, P3, firmware/root/MCU/70mai or T3 work in this gate.

## Finish definition
The P2 shell reads as a final-product Darbak UI while unavailable backends remain truthful, passes API25/1024x600 regression and visual checks, and is checkpointed in GitHub.
