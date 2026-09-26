# Darbak OS References

This is a permanent reuse log. Before building from scratch, check here and upstream sources. Record exact source/version/license when code is copied or adapted.

| Reference | Use in Darbak | Type | Compatibility / rule |
|---|---|---|---|
| OsmAnd / OsmAnd API & AIDL | Offline maps, search, navigation, favorites/places, routing integration | Engine/API/reference | Primary map engine; wrap it; avoid unnecessary core forks; verify exact API25-compatible build |
| Open Launcher (dw2lam/openlauncher) | Offline-first/OEM-like launcher concepts, GPS/sunset ideas | Concept/reference | Adapt lightweight ideas only |
| Dashline (metehankaygsz/dashline) | Weak/old Android head-unit UI patterns | Concept/reference | Valuable old-Android/lightweight reference |
| femto-car-launcher (seijikohara) | Stable/test separation, recovery/backoff, reduced-motion ideas | Concept/reference | Modern Android; ideas only unless individual code is independently compatible |
| BMW iDrive Launcher references | Crash/ANR/black-screen monitoring, staged recovery | Concept/reference | Reimplement appropriately for API25 |
| OpenMasjidKiosk | HOME/kiosk and hidden admin gesture concepts | Concept/reference | Pattern only |
| Minimal Car Launcher | Lightweight GPS/quick-card/startup concepts | Concept/reference | Inspect before any code reuse |
| Helm head-unit platform | Launcher/hardware/MCU separation | Architecture concept | Newer hardware; ideas only |
| KSW Car Project | MCU communicator/event patterns | Architecture concept | Reference only |
| LibAuto | Independent car state/night/GPS/key channels, low-write ideas | Concept/reference | Inspect before reuse |
| CarRadio/TWUtil/TWClient | Evidence/reference for T3 vendor APIs | Platform reference | FM radio excluded; investigate only non-radio functions useful to Darbak |
| 4PDA/XDA Allwinner T3 material | SWC, sleep/ACC, factory settings, USB, MCU mismatch evidence | Community reference | Anecdotal/device-specific; verify on exact unit |
| DoFun / T3 Firmware channels | Recovery/tools/APKs/boot-animation/TS-T3 ideas | Tool/reference | No DoFun firmware approved for this unit |
| t3-p3 Android 7.1.1 dumps | Compare partition/files/build family | Comparison only | NEVER treat an external dump as Golden Backup for this unit |
| Breadcrumb/Colota/OSMTracker-style trip projects | Continuation, event waypoints, merge/split ideas | Concept/reference | Use concepts only after fit review |

## Reuse record format
For every code-level reuse add:
- Source URL/repository and commit/tag
- File/component
- License
- Darbak destination
- Changes made
- API25/ARMv7/resource verification
- Test result

## Rule
"Available" is not the same as "suitable." Prefer small proven pieces; reject dependencies that raise minSdk, require unsupported ABI, consume excessive RAM/GPU, or duplicate a stable OsmAnd/Android capability.
