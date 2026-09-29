# Next Task — P3 Guardian monitoring foundation bundle IN PROGRESS

## Work-mode policy
Development is performed in the main chat/GitHub. Work is reserved for a later consolidated heavy verification gate; do not spend Work quota on each small class.

## Current bundle
Passive Supervisor is verified at65/65. Development now begins the monitoring foundation without starting automatic monitoring.

Added:
- GuardianHeartbeat: immutable caller-supplied component/sequence/monotonic-time sample.
- GuardianLivenessPolicy: pure LIVE/LATE/STALE/UNKNOWN classification and mapping to Guardian health.

## Safety boundary
These classes do not read Android clocks, create threads, timers, handlers, executors, services, receivers or listeners. They do not poll, mutate GuardianRegistry, restart anything, persist data, use network/sensors, or execute recovery. Time is supplied explicitly by the future caller.

## Bundle direction
Continue locally with bounded monitoring primitives and event history before one consolidated Work verification. Preserve API25/ARMv7/~1GB/1024x600 constraints and all accepted P2 behavior.

## STOP condition for Work
Do not send this partial bundle to Work yet. Open one consolidated verification gate only after the monitoring-foundation bundle is complete enough to justify full API25 regression.
