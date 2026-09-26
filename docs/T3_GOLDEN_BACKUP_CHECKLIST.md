# Darbak T3 Golden Backup Checklist

Do not perform low-level Darbak integration before this gate passes on the real unit.

## Read-only identity
- System Version
- MCU Version
- getprop
- Build/fingerprint
- kernel
- API/Android version
- ABI/CPU
- RAM/eMMC size
- display resolution
- /dev/block/by-name and partition sizes
- root status without modifying system
- USB/OTG capability

## Factory snapshot
Capture every relevant Factory/Extra Settings page and current working behavior: CAN/car selection, audio/AMP, Bluetooth, GPS, display/touch, steering keys, USB, ACC/sleep and other hardware options.

## Backup
As safely available:
- full eMMC
- boot
- recovery
- system
- vendor
- private
- env
- misc
- every discovered relevant partition
- /system/etc/goc/
- touch/calibration and OEM hardware config
- Export All Settings
- important OEM APKs/services

## Integrity
Create a manifest containing file name, byte size and SHA-256. Keep one immutable Golden copy and a separate working copy.

## Recovery
Document exact-board recovery method. Recovery/SD/Phoenix/FEL/OTG are not assumed until proven for this board. No test-point/eMMC short without exact-board documented method.

## Prohibited
- MCU flash/update
- random "T3" firmware
- treating T3L/T3-P1 as compatible
- destructive root solely to obtain backup before safer paths are evaluated
- using an external t3-p3 dump as the unit's recovery image

## Candidate firmware search fingerprint
t3-p3 + sun8iw11p1 + V8.3.2 + ZH5 + 1024x600, then match System Version, Android version, MCU family and hardware configuration. Candidate is not approved until exact-unit evidence supports it.
