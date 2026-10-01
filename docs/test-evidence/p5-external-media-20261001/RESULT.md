# P5 External MediaSession focused gate — PASS / STOP

Date:2026-10-01. Incoming main:`d61cfc2a0385018e4e53a6d72c018cdcdd7be7e1`.
Verified code:`7c4bf543eb11a3742dfde7ed2c75d4e1ca69faa2`.
[Successful run36818005050](https://github.com/abo-sultann/DarbakOS/actions/runs/36818005050), job110227196579, attempt1.

## Proven causes and bounded corrections
1. Requested run36817089400 built/linted successfully, then stopped before bootstrap with Python SyntaxError. Two fixture assertions contained literal backslash-n separators. The first syntax-only correction is0550259aa474405ff92be7cf66a28c2942b0f2e9; original log/provenance are in `before-fix/`.
2. Run36817725413 then completed9 +1 passing tests, but the runner rejected JUnit's `OK (1 test)` because it required plural `tests`. Final correction accepts the singular only for expected count1 and retains exact expected count/failure rejection. Raw result and erroneous rejection are in `before-parser-fix/`.

`runner-fix.diff` is the complete code change. No application, instrumentation, fixture, workflow, build input or P4/OsmAnd change. `source-audit.json` checks89 unchanged inputs; `result-parser-check.txt` records acceptance of actual logs and rejection of wrong count/failure/incomplete output.

## Final unchanged gate
- Build/Lint PASS:0 errors,17 warnings.
- API25/default/x86,1024x600/160dpi/1GB:9/9 Shell+Snapshot in4.064s;1/1 external integration in0.639s;1/1 granted-access idle in0.518s.
- Android Settings access grant captured in XML. External UID10065 versus Darbak10063; session initially PAUSED, no autoplay on observation, explicit Play/Next produce PLAYING and updated metadata. Fixture removal returns to stopped idle with transport disabled.
- Both Home screenshots reviewed: retained Arabic RTL, legible truthful media states and no observed overlap. Empty crash buffer/no app ANR. Existing TripRuntimeService presence check passed.
- App APK71,475 bytes, delta0; all10 ZIP-entry payloads identical to incoming. Final APK SHA256:`f9c3e15d5511901a3a650769a72380efbb692212ef053acd1bce2108950d0ffd`.
- Artifact11142152466 ZIP SHA256:`b3f9e2218987abea3bd3cb5011bf3299706e83a0b6957d72484ebf21aaad1d5d`; expiry2026-10-15. Full captured logcat is retained here; provenance/verification/hashes accompany the raw evidence.

## Scope limits
No Full Regression, Guardian suites, separate P4/OsmAnd suite or extra phase. Two necessary reruns of the same gate followed proven failures; the successful gate was not rerun.
Fixture exercises real MediaSession IPC but generates no audio. Explicit Play/Next passed; Pause/Previous enabled-state only. No actual third-party player/version, full device reboot/wake, audio output, ARMv7/Test Station/T3 acceptance. Home screenshots only; Media-panel fit is programmatic. No PSS/CPU benchmark was added.

The owner's P5 diagnosis request superseded the stale P4 task file. P4 remains closed/unchanged. This gate is complete; P5 is not fully phase-closed. Commit/push evidence/status, then STOP.
