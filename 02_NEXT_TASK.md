# Next Task — P3 Guardian monitoring foundation bundle IN PROGRESS

## Work-mode policy
Main chat/GitHub performs implementation and lightweight review. Work is reserved for one consolidated API25 verification gate after this bundle is complete.

## Implemented in this bundle
- GuardianHeartbeat: immutable caller-supplied component/sequence/monotonic sample.
- GuardianHeartbeatRegistry: latest accepted heartbeat per component; rejects sequence/time regression.
- GuardianLivenessPolicy: pure LIVE/LATE/STALE/UNKNOWN classification -> Guardian health.
- GuardianLivenessSnapshot: immutable all-component classification and explicit caller-driven health bridge.
- GuardianMonitorConfig: conservative configurable thresholds (default late5s, stale15s; to be calibrated on real T3).
- GuardianEvent + GuardianEventJournal: fixed64-event in-memory ring; immutable retained snapshots, latest lookup, no persistence.
- GuardianMonitorStep: one caller-driven chain from heartbeat through liveness/health/Supervisor, journaling only meaningful health transitions.

## Safety boundary
No production clock reads, Service/Receiver, thread, timer, Handler, Executor, scheduler, polling, automatic listener, disk/network/sensor access or recovery execution. No UI change. No automatic watchdog yet.

## Remaining before consolidated Work gate
Perform source-level bundle review and add/prepare focused regression coverage for heartbeat ordering, threshold boundaries, ring overwrite/order/retention, no-op cycles, health transitions, config sanitization and complete MonitorStep composition. Then open one Work gate that runs focused tests plus the existing full API25/1024x600 regression once.

## Do not start
No automatic watchdog/service, recovery execution, persistence/report export, source adapters, OsmAnd/GPS/Trip, Media, Vehicle, T3/root/firmware/MCU work.

## Work STOP condition
Do not send partial increments to Work. One consolidated verification only after the bundle is marked READY FOR WORK.
