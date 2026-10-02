# P9 Golden Backup Manifest

Status: NOT VERIFIED / RECOVERY LOCKED.

This manifest is a gate, not proof that a backup exists.

## Exact device identity
Fill only from `scripts/p9_t3_baseline.py` evidence:
- Baseline capture ID:
- Android/API:
- Build fingerprint:
- Board / product / device / model:
- CPU ABI:
- System/MCU identifiers exposed read-only:

## Golden Backup set
For every acquired backup image/file record:
| Item | Acquisition method | Size | SHA-256 | Verified readable |
|---|---|---:|---|---|
| TBD | TBD | TBD | TBD | NO |

## Recovery path
- Procedure source/reference: NOT VERIFIED
- Exact target compatibility: NOT VERIFIED
- Required tools/drivers: NOT VERIFIED
- Non-destructive validation performed: NO
- Actual recovery rehearsal/independent verification: NO

## Eligibility rule
Recovery stays LOCKED unless:
1. exact T3 identity is recorded;
2. the Golden Backup set is acquired and every item has SHA-256;
3. the recovery procedure is independently verified for this exact device;
4. recovery evidence is documented.

No firmware/MCU/kernel flash, destructive root, OEM hiding, boot replacement or system-app removal is authorized by this file.
