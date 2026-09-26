# Darbak OS — Master Plan for Work v1.0

## Mission
Build Darbak OS as the unified in-car experience for the owner's existing Allwinner T3 head unit. Android remains the stable hardware-compatibility base; Darbak replaces the normal day-to-day experience.

## Hard target
- Device: t3-p3
- SoC family: sun8iw11p1 / Allwinner T3
- CPU: ARM Cortex-A7 / ARMv7
- GPU: Mali-400
- RAM: ~1 GB
- Display: 1024x600 landscape
- Baseline: Android 7.1 / API 25
- Arabic RTL first, offline-first, lightweight.
Do NOT treat T3L, T3-P1, or a generic "T3" firmware/device as compatible.

## Non-negotiable safety gate
No low-level modification, firmware flash, MCU flash, destructive OEM removal, kernel replacement, or risky root action before the real unit has:
1. Factory Settings snapshot.
2. Read-only ADB hardware/system scan.
3. System Version + MCU Version + getprop.
4. Partition map and eMMC size.
5. Root status and USB/OTG capability check.
6. Darbak T3 Golden Backup as complete as safely possible.
7. SHA-256 manifest verification.
8. A documented recovery path appropriate to the exact board.
9. Baseline functional tests.
MCU update/flash is currently prohibited. External t3-p3 dumps are comparison references only.

## Golden Backup scope
As available and safely readable: full eMMC image; boot, recovery, system, vendor, private, env, misc and all discovered partitions; partition map; /system/etc/goc/; touch/calibration and relevant OEM configuration; Export All Settings; important OEM APKs/services; getprop; build/fingerprint; System Version; MCU Version; partition sizes; hashes. Preserve factory-setting screenshots. PRIVATE is important but is not assumed to be a direct MCU-controller dump.

## Firmware policy
Current search branch only: t3-p3 + sun8iw11p1 + V8.3.2 + ZH5 + 1024x600. ZH5/V8.3.2 are candidates until verified against this exact unit. DoFun is a tools/recovery/APK/idea reference, not an approved firmware source. Booting is not proof of hardware compatibility.

## Development/test path
Never use the real T3 as the first experiment target.
Build/develop -> laptop/emulator initial tests -> 1024x600/API25/RTL/navigation/crash/resource tests -> Darbak Test Station (Acer/BlissOS) where useful -> candidate APK -> real T3 validation -> Stable.
The T3 is the final acceptance gate.

## Work quota policy
Work usage is limited. Do not spend Work quota rediscovering requirements, doing broad research, or debating closed decisions.
Work in small closed batches. Before starting the next batch:
- complete the current smallest useful unit,
- test it,
- commit/push it,
- update 01_CURRENT_STATUS.md,
- update 02_NEXT_TASK.md,
- append CHANGELOG.md and TEST_RESULTS.md where relevant.
Never leave important progress only inside a Work session. GitHub is project memory and source of truth.

## Reuse-first policy
Before implementing a component from scratch, check REFERENCES.md and suitable upstream projects. Prefer adapting a proven small component/concept when compatible with API25/ARMv7/~1GB/1024x600/RTL. Do not import an entire project or heavy dependency just because it exists. When code is reused, record source, license, modifications and destination component.

## User experience
Cold Boot -> Darbak signature/init -> Darbak Home. Normal use must not expose stock Android.
Primary user areas: Home, Map, Media, Vehicle, Apps. Settings is secondary; technical Admin is hidden by intentional gesture with no PIN. Emergency Recovery is independent.
Home is a dashboard, not an app grid: large km/h speed, navigation state, media, quiet vehicle state, useful quick actions.
Normal state is quiet; show actionable abnormalities rather than constant OK indicators.
No FM radio requirement. No weather/news/calendar/email core features. No heavy blur/video backgrounds.

## Core behavior
- OsmAnd is the map/navigation engine; Darbak wraps it rather than rebuilding routing/maps.
- Darbak Search uses OsmAnd/offline data where possible; My Places/Favorites are unified, not duplicated.
- Trip Recorder is automatic and independent of OsmAnd UI/navigation.
- Media never auto-plays after cold boot/wake; restore state only.
- Vehicle values always carry provenance/freshness; stale sensor values are never shown as live.
- Standby is a lightweight calm driving display and never stops trip recording.
- Voice, if implemented, is one-shot and physically triggered where proven; no always-listening assistant.
- Day/Night Auto uses local date + GPS + sunrise/sunset; no light/headlight/MCU dependency.
- Search and interactions simplify while MOVING; STOPPED temporary does not suddenly unlock everything; PARKED allows richer interaction.
- External storage is preferred for large data, but Darbak core remains internal and functional without it.
- Loss of one secondary source must not take down the OS experience.

## Architecture responsibilities
Core/Boot/Navigation/Common; Home; Maps integration; Media; Vehicle; Apps; Settings; Guardian; Diagnostics; Health; Update; Admin; Trip/Position; Data; Display/Touch; Audio; Input; Hardware abstraction; Storage/Backup; Connectivity.
Do not split into processes/APKs unless isolation benefit justifies RAM cost.

## Reliability
Guardian monitors crash/ANR/black-screen/liveness/freshness and uses staged recovery. Preserve Known Good/Stable separately from TEST. Updates require compatibility checks, snapshot, verification and rollback path. A degraded external source is not Safe Mode. Never display stale navigation/TPMS/GPS/vehicle data as current.

## Session/power
Support Sleep, clean shutdown and sudden power loss without assuming which the T3 actually uses. Continuous safe-save plus fast final save. Wake restores healthy context quickly; Cold Boot always lands on Home. Navigation may offer Quick Resume rather than blindly reopening. Trip continuity is independent.

## Data/storage
Clear ownership of data, live state separate from history, safe chunked trip recording, rotating logs, schema/config migrations with rollback, event bus, retention rules, emergency internal buffer when external storage disappears. No Firebase/cloud dependency.

## Update/acceptance
Stable requires: build -> initial/emulator tests -> real T3 install -> boot/wake -> Home -> touch -> GPS/Trip -> Media -> essential Vehicle -> resource/crash check. Module updates test module + dependencies. T3 stability beats newest version.

## First execution phases
P0 Repository/process baseline.
P1 Laptop development/test environment and minimal Darbak shell at API25/1024x600 RTL.
P2 Home/navigation shell and design system with fake/test data only.
P3 Core state/services + Guardian basics + logging/reporting.
P4 OsmAnd integration + Position/Trip.
P5 Media.
P6 Vehicle data integration.
P7 Apps/Settings/Standby/alerts.
P8 Update/Admin/Recovery integration.
P9 Real T3 commissioning + Golden Backup/recovery verification BEFORE deep system integration.
P10 T3 integration, OEM hiding/autostart/boot experience only after P9 passes.
P11 Stable acceptance.

## Stop conditions
If a task requires firmware/MCU/kernel/destructive root changes, stop that task and document why. If an upstream dependency requires >API25/unsupported ABI/excessive resources, do not force it into the project. If the exact T3 capability is unknown, mark UNKNOWN and test it later rather than assuming.

## Work instruction
Start only from 02_NEXT_TASK.md. Do not redesign the project or reopen closed decisions unless testing proves a conflict. Finish, test, push and checkpoint every batch so another ChatGPT session can continue immediately.
