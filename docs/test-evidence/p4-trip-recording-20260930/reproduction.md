# Proven monotonic-order defect

At task base `559e51aa9ab9530b12c0dd40b9c4e6da31c49a1e`, pause/resume and finish/start create an empty segment. The old accept method checked time only against the current segment; both time9999 and time10000 were therefore accepted after time10000. All four inputs are fresh and otherwise valid, isolating the ordering defect.

`before-fix.txt`: standalone Java reproduction exited1 with four violations. `after-fix.txt`: same reproduction exited0 with all four inputs rejected after the narrow fix. `TripMonotonicRepro.java` is retained for reproducibility. This local proof is separate from Android instrumentation and does not substitute for it.

From the repository root, compile PositionFix.java, TripPoint.java, PositionQualityPolicy.java and TripRecorder.java with this reproduction using `java -m jdk.compiler/com.sun.tools.javac.Main -d <temporary-classes> <source-files>`, then run `java -cp <temporary-classes> TripMonotonicRepro`.

The fix retains the last accepted timestamp across segments and clears it only with reset. The Android TripRecorderTest adds the same boundary protection, exact sequence assertions, quality/gap boundaries and immutable snapshot/reset checks. Original supplied assertions remain intact.
