#!/usr/bin/env python3
"""Static safety checks for P9 baseline/backup gate; does not require hardware."""
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
collector=(ROOT/"scripts/p9_t3_baseline.py").read_text()
manifest=(ROOT/"docs/P9_GOLDEN_BACKUP_MANIFEST.md").read_text()
required=["getprop","wm","size","density","df","pm","list","packages","/proc/cpuinfo","SHA256SUMS"]
for token in required: assert token in collector, token
for forbidden in ["adb root","adb remount"," fastboot "," flash ","dd if=","su -c","pm uninstall"]:
    assert forbidden not in collector.lower(), forbidden
for token in ["NOT VERIFIED","Recovery stays LOCKED","SHA-256","exact T3 identity"]:
    assert token in manifest, token
print("PASS: P9 collector is read-only and Golden Backup gate defaults locked")
