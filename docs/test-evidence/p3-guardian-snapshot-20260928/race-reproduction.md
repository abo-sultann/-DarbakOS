# Snapshot consistency defect evidence

Before: incoming commit `deb45ec5104e9d43af22a10211abb36a202a5763`, unchanged GuardianSnapshot.
After: tested fix `3709e356f0fdc623b86f7895b18c1ff1ef553f05`.

The standalone probe uses Java17 and only the four Guardian classes. It sets all components healthy, starts a finite test writer toggling CORE between FAILED/HEALTHY, and compares captured core health with captured overall. Other components remain healthy, so those two values must match. Writer is stopped/joined and registry reset even on failure.

Before: exit1, first captured core was FAILED at revision242 while overall was HEALTHY (see before-fix.txt). This proves reads/aggregate could observe different registry instants. Exact capture/revision numbers depend on scheduling.
After: same probe exit0 after20000 captures (after-fix.txt). The fix holds the existing registry monitor across all component reads and aggregate calculation. No production thread is introduced.

Compile from a checkout of the version being tested, placing class outputs in a scratch directory:

```sh
java -m jdk.compiler/com.sun.tools.javac.Main -d /tmp/guardian-probe app/src/main/java/com/abosultan/darbakos/core/GuardianState.java app/src/main/java/com/abosultan/darbakos/core/GuardianRegistry.java app/src/main/java/com/abosultan/darbakos/core/GuardianPolicy.java app/src/main/java/com/abosultan/darbakos/core/GuardianSnapshot.java docs/test-evidence/p3-guardian-snapshot-20260928/GuardianSnapshotRaceProbe.java
java -cp /tmp/guardian-probe GuardianSnapshotRaceProbe
```

For the before version, use the same saved probe file with that version's four production sources. The Android regression additionally performs20000 captures while a test-only writer atomically updates all components and resets the registry; each captured health/revision/overall must agree. Its writer is joined before teardown. All43 instrumentation tests passed on API25. The local reproduction is JVM evidence, not a pre-fix Android run or an app crash.
