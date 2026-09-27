# Darbak OS

Unified in-car operating experience for the owner's Allwinner T3 head unit.

**Target:** t3-p3 / sun8iw11p1 / Android 7.1 API25 / ARMv7 / ~1GB RAM / 1024x600 landscape.

## Start here
1. `00_Darbak_OS_Master_Plan_Work_v1.0.md`
2. `01_CURRENT_STATUS.md`
3. `02_NEXT_TASK.md`
4. `REFERENCES.md`
5. `TEST_RESULTS.md`

## Critical rule
The real T3 is the final validation target, not the first experiment target. No deep system/firmware work before the exact unit's Factory Snapshot, read-only scan, Golden Backup, SHA-256 verification and recovery path are established.

GitHub is the durable source of truth so work can continue across ChatGPT/Work quota boundaries.

## P1 test shell
Build/emulator steps: [docs/P1_BUILD_AND_TEST.md](docs/P1_BUILD_AND_TEST.md).
Use `01_CURRENT_STATUS.md` and `TEST_RESULTS.md` for the actual validation state.
Verified P1 Home: [API25 emulator screenshot](docs/test-evidence/p1-20260927/home-1024x600.png).
