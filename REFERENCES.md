# Darbak OS References

Permanent reuse log. Before building from scratch, check these sources and inspect the exact upstream version. When code is reused, record source commit/tag, license, Darbak destination, modifications and API25/ARMv7/resource test result.

| Reference | URL | Use in Darbak | Type | Compatibility / rule |
|---|---|---|---|---|
| OsmAnd | https://github.com/osmandapp/OsmAnd | Offline maps/search/navigation/favorites/routing integration | Engine/API/reference | Primary map engine. Prefer AIDL/API/wrapper; avoid unnecessary core fork. Pin an API25/ARMv7-compatible version after testing. |
| Open Launcher | https://github.com/dw2lam/openlauncher | Offline-first/OEM+ launcher ideas, GPS/dashboard/day-night patterns | Concept/reference | Inspect small reusable patterns only; Darbak keeps fixed lightweight automotive cards rather than importing its full widget system. |
| Dashline | https://github.com/metehankaygsz/dashline | Old/weak Android head-unit implementation patterns, classic Views, media/app shortcuts, adaptive cards | High-priority code/reference candidate | Runs on Android 4.4+ and no Play Services. Current project is GPLv3; code reuse requires license review before copying into Darbak. Concepts may be reimplemented independently. |
| Femto Car Launcher | https://github.com/seijikohara/femto-car-launcher | Stable/nightly separation, test discipline, glanceable UI, failure/backoff ideas | Concept/reference | Requires Android 13/API33; do not import its modern stack into T3/API25. |
| Helm head-unit platform | https://github.com/HelmMobile/helm | Launcher/hardware/MCU separation ideas | Architecture concept | Newer hardware; ideas only until individually verified. |
| LibAuto | https://github.com/f1xpl/LibAuto | Car-state/night/GPS/key channel concepts | Concept/reference | Inspect exact code/license/Android requirements before reuse. |
| OpenMasjidKiosk | Search/verify upstream before code reuse | Kiosk/HOME + hidden admin gesture concept | Concept/reference | Pattern only until exact upstream/license is logged. |
| BMW iDrive Launcher references | Search/verify exact upstream before code reuse | Crash/ANR/black-screen monitoring, staged recovery | Concept/reference | Reimplement for API25 unless an exact compatible source is verified. |
| Minimal Car Launcher | Search/verify exact upstream before code reuse | Lightweight GPS/quick-card/startup concepts | Concept/reference | Do not copy until exact upstream/license is recorded. |
| KSW Car Project | Search/verify exact upstream before code reuse | MCU communicator/EventCenter patterns | Architecture concept | Reference only. |
| CarRadio / TWUtil / TWClient references | Search/verify exact upstream before code reuse | Evidence/reference for T3 vendor APIs | Platform reference | FM radio excluded. Investigate only non-radio T3 functions useful to Darbak. |
| 4PDA/XDA Allwinner T3 material | Community sources | SWC, sleep/ACC, factory settings, USB, MCU mismatch evidence | Community reference | Device-specific/anecdotal; never generalize without exact-unit validation. |
| DoFun / T3 Firmware channels | Telegram/community sources | Recovery/tools/APKs/boot-animation/TS-T3 ideas | Tool/reference | No DoFun firmware approved for this unit. |
| t3-p3 Android 7.1.1 dumps | External reference dumps | Partition/file/build comparison | Comparison only | NEVER substitute for this unit's Golden Backup. |
| Breadcrumb/Colota/OSMTracker-style trip projects | Upstreams to be pinned when used | Continuation, event waypoints, merge/split ideas | Concept/reference | Concepts only until fit/license review. |

## Reference selection order
1. Existing Android/API25 capability.
2. OsmAnd capability when map/navigation/location-related.
3. A proven lightweight API25-compatible component/reference.
4. Adapt a small upstream component.
5. Implement from scratch only when the above do not fit.

## Mandatory code-reuse record
For every code-level reuse append:
- Source repository URL
- Commit/tag
- Source file/component
- License
- Darbak destination
- Why reuse is preferable
- Changes made
- minSdk/API/ABI/dependency check
- RAM/performance observations where applicable
- Test result on laptop/Test Station/T3 as stages become available

## Rules
- "Available" is not "suitable."
- No dependency may silently raise minSdk above API25.
- Reject unsupported ABI/native libraries or excessive RAM/GPU cost.
- Do not duplicate stable OsmAnd/Android capabilities.
- Never import a full launcher simply to obtain one useful component.
- Concept inspiration does not justify copying code; code-level reuse requires an exact source and license check.
