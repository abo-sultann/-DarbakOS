# Next Task — P2 Apps-shell verification

## Start point
Prior P2 Home/navigation/media gates passed. Main now contains one bounded P2 increment: the Apps placeholder section exposes three static preview actions (Recent, Favorites, App management) and only shows them inside Apps. No PackageManager discovery, install/uninstall action, external launch or service was added.

## Goal
Verify only Apps-shell presentation/isolation and prior regressions.

## Required verification
- Build/Lint and existing API25/1024x600 tests.
- Open Apps and inspect screenshot for clipping/overlap and readable RTL labels/touch targets.
- Confirm the three preview actions appear in Apps only and do not appear in Map/Media/Vehicle/Settings/Home.
- Confirm they are clearly preview/test UI and perform no install/uninstall/external launch.
- Regress Home, quick actions, Back/Home, navigation TEST card, stopped Media/no-autoplay and vehicle test/stale states.
- If this increment causes a proven defect, fix only that defect and rerun affected checks.
- Record evidence/results; update CURRENT_STATUS.md, TEST_RESULTS.md and CHANGELOG.md.
- Commit + push, then STOP.

## Constraints
No PackageManager enumeration, APK install/uninstall, external app launch, permissions, services, OsmAnd/GPS/Trip, playback, Vehicle integration, new dependencies, P3, firmware/root/MCU/70mai or T3 work.

## Finish definition
The static Apps preview is visually/semantically verified and isolated to Apps at API25/1024x600, with prior P2 behavior intact, then checkpointed; otherwise document the exact blocker.
