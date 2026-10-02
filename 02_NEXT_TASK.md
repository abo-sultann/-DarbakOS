# Next Task — P9 Physical T3 Baseline Session

Updated: 2026-10-02.

## Objective
Run the first physical, non-destructive Darbak OS commissioning session on the exact Allwinner T3 head unit.

## Session order
1. Connect the T3 to a computer with ADB debugging authorized. Do not root/remount.
2. Run `python3 scripts/p9_t3_baseline.py` and preserve the generated `device-evidence/` folder.
3. Review exact Android/API/build/board/product/device/model/ABI/display/storage/system identifiers before installing anything.
4. If the baseline matches the supported API25/ARMv7/1024x600 target and exposes no blocker, install only the current TEST APK artifact and perform a bounded smoke: launch, Home RTL/layout, navigation, Settings/Admin read-only surfaces, Standby enter/exit, and no crash.
5. Do not enable OEM hiding/autostart/boot/system integration.
6. Golden Backup/recovery remains LOCKED until exact-device backup acquisition and independently verified recovery procedure are documented and hashed.

## Stop conditions
Stop immediately on unexpected reboot, display corruption, package conflict affecting OEM functions, storage anomaly, or any request for root/system modification.

## Exit
Physical baseline evidence captured + bounded TEST APK smoke recorded. Golden Backup gate remains a separate prerequisite before P10 deep integration.
