# Current Status

State: P3 CORE PUBLISH/SUBSCRIPTION VERIFIED — BATCH CLOSED / STOP
Updated: 2026-09-28.
Target: t3-p3 / sun8iw11p1 / Android7.1 API25 / ARMv7 / ~1GB / 1024x600.

## Current accepted UI
- The current P2 interface is the final Darbak OS UI, not a disposable prototype, per owner instruction. Historical TEST/fake-copy records below in TEST_RESULTS are superseded for user-facing behavior.
- Home: speed unavailable; no active navigation route/navigation unavailable; media has no selected track and is stopped; vehicle data unavailable. No fabricated live values or user-facing TEST/experimental/preview wording.
- Apps: Recent/Favorites/App Management appear only in Apps, ordered RTL and visibly disabled while their backends are unavailable.
- Existing Arabic landscape navigation/quick actions/Back/Home/recreation/cold restart preserved. No automatic playback or app service/MediaSession.
- Existing platform Views/Java and recorded reuse choices retained; no dependencies/services/backend added.

## Core State foundation
- DarbakState provides immutable unavailable speed/navigation/vehicle and stopped media at cold boot.
- CoreStateStore owns one in-process snapshot, synchronized reads/reset. Fresh Activity initializes cold state; recreation preserves it. No live-source/UI binding or background work added.
- Immutable revisions and synchronous in-process publish/listeners verified: exact snapshot delivery, duplicate prevention, removal, null ignored, cold reset to revision0. Six focused tests added; all prior tests retained; no application defect found.

## Verified checkpoint
- Tested code: `31ce9bc83cd37760433da482c2f4cce3cd793673`.
- Successful run: https://github.com/abo-sultann/DarbakOS/actions/runs/36361519633
- Build/Lint: 0 errors,17 warnings; instrumentation14/14 (all8 prior +6 publish); quick-action returns6/6.
- API25/x86,1024x600/160dpi: Home/Apps/restart images pixel-identical to prior Core/P2; RTL/fit/navigation/no-autoplay pass. No new background/Service/permission/dependency/native behavior; no observed crash/ANR.
- APK23,319 bytes (+712); PSS9,180KB (+173); launch475ms versus301ms. Separate single emulator snapshots, not causal benchmarks.
- Evidence: `docs/test-evidence/p3-publish-20260928/`; details/history: TEST_RESULTS.md.
- Physical T3/ARMv7/Test Station and Stable acceptance remain outstanding.

## Not yet done
- Further P3 work (Guardian, logging/reporting and source integration): not started.
- OsmAnd, Trip/Position, media playback, vehicle integration, Guardian, updater or OEM integration.
- Laptop-specific and Test Station runtime/resource validation.
- Real T3 commissioning, Factory Snapshot, Golden Backup, verified recovery and final acceptance.
- No firmware or MCU flash approved; all master-plan safety gates remain in force.

## Current gate
The requested P3 Core publish/subscription verification batch is complete. STOP here.
Do not start Guardian, logging/reporting, other P3 work, hardware work or another batch.
`02_NEXT_TASK.md` records closure, not a new assignment. No Stable/T3 acceptance is claimed.

## Continuation
Read README and its listed files in order. GitHub is the sole project-state authority.
Complete/test/commit/push each bounded batch and update state files before the next batch.
