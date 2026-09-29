# Guardian Monitor Session focused verification — 2026-09-29

Incoming main: `16bb89d08b843189ae1de55707abfef65ef21526`.
Test/fix commit: `018b509aaa00cdb0df7f424cea3a6c24342cf443`.

Reused REFERENCES.md decisions and the existing MonitoringFocusedChecks pattern. No external code or dependency added. The six new pure-Java checks are called unchanged by GuardianMonitorSessionTest on API25; its four supplied tests remain intact. Local command: `python3 scripts/check_monitoring.py SessionFocusedChecks` (JDK17 compiler module).

## Proven defect and narrow fix

`before-fix.txt`: five checks pass; the saturation check fails because an old `Long.MAX_VALUE` heartbeat is accepted after reset at the saturated generation. The counter does not overflow, but reusing its token violates cold-boot isolation.

GuardianMonitorSession now marks the generation exhausted when reset cannot allocate a distinct next generation. It keeps the counter at MAX_VALUE, clears heartbeats/journal/health as before, and rejects further heartbeat input through both overloads. The final distinct generation is still usable before it is reset. Reads and explicit UNKNOWN evaluations remain safe after exhaustion. No token wrap/reuse, clock, worker or recovery was added. This is the only production correction.

Test-only reflection sets the counter to MAX_VALUE-1 to exercise the boundary without iterating an impractical number of resets. There is no production test hook. `after-fix.txt`: all six shared checks pass.

## Focused coverage

| Check | Assertions |
|---|---|
| Generation and component isolation | Initial0, three resets, exact increment, all5 components, old/future rejection, current acceptance, duplicate/sequence/time rejection, convenience overload, frozen prior samples |
| Mixed diagnostics and retained results | Exact LIVE/LATE/STALE/UNKNOWN composition, health/revision identity, every stored heartbeat, ordered event component/revision/time/type/health, assessment counts and SAFE_MODE recommendation target; later updates/no-op evaluation/reset leave prior snapshots/results unchanged |
| Null/empty behavior | Null guardian/config/component, minimum journal capacity, empty evaluations at negative/extreme levels, conservative UNKNOWN, negative heartbeat normalization, reset does not touch unrelated singleton health |
| Explicit evaluation only | Heartbeat/config/generation/snapshot reads do not evaluate; FAILED remains after fresh input until explicit evaluate; recommendation does not perform recovery; bounded journal capacity |
| Generation saturation | MAX_VALUE-1 -> MAX_VALUE, last current generation accepts input, reset at MAX_VALUE rejects delayed MAX token and convenience input, no MIN_VALUE wrap, repeated reset/UNKNOWN evaluation safe, old diagnostics retained |
| Session synchronization |200 barrier-coordinated writer/reader rounds with reset, delayed input, current heartbeats, explicit evaluation and snapshot; generation/health/revision/heartbeat/event coherence; retained snapshot; joined test-only worker |

The bounded concurrency check covers valid interleavings through this session's public operations; it is not an exhaustive schedule proof or a guarantee against independent external mutation of the supplied GuardianRegistry/journal.

## Single Android gate selection

Build/Lint preceded10 focused Session tests. Only after they passed, one bounded regression selected16 existing Monitoring Foundation tests and58 prior P2/Core/Guardian tests. Session tests were not repeated in that regression. Seven unchanged giant proofs were reused from `ee62972ec8a0372f7ec682220bdc21e183b30405`, run36475157726; all12 pinned source fingerprints matched before reuse. No1024/6144 suite was regenerated.

Run36596597821 attempt1 passed: Session10/10 in0.069s, bounded regression74/74 in3.798s and6/6 UI navigation returns. Build/Lint0 errors/17 unchanged warnings. Raw results, resource comparison, artifact provenance and unchanged Home/Apps/restart screenshots are saved beside this file. Gate closed; STOP.
