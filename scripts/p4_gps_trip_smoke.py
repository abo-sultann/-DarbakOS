#!/usr/bin/env python3
"""Focused API25 gate for P4 GPS, trip persistence and lightweight OsmAnd bridge."""
from pathlib import Path
import json
import re
import subprocess
import time
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'test-evidence'
OUT.mkdir(exist_ok=True)
PACKAGE = 'com.abosultan.darbakos.test'
ACTIVITY = PACKAGE + '/com.abosultan.darbakos.MainActivity'
serials = [line.split()[0] for line in subprocess.check_output(['adb', 'devices'], text=True).splitlines()[1:]
           if line.endswith('\tdevice') and line.startswith('emulator-')]
assert len(serials) == 1
ADB = ['adb', '-s', serials[0]]


def adb(*args, binary=False):
    return subprocess.check_output(ADB + list(args), text=not binary, timeout=60)


def save(name, value):
    path = OUT / name
    path.write_bytes(value) if isinstance(value, bytes) else path.write_text(value)


def dump():
    adb('shell', 'uiautomator', 'dump', '/sdcard/darbak-ui.xml')
    xml = adb('shell', 'cat', '/sdcard/darbak-ui.xml')
    save('p4-gps-ui.xml', xml)
    return ET.fromstring(xml)


def node(tree, resource):
    found = [n for n in tree.iter('node') if n.get('resource-id', '').endswith(':id/' + resource)]
    assert len(found) == 1, resource
    return found[0]


assert adb('shell', 'getprop', 'ro.build.version.sdk').strip() == '25'
adb('shell', 'wm', 'size', '1024x600')
adb('shell', 'wm', 'density', '160')
apk = ROOT / 'app/build/outputs/apk/debug/app-debug.apk'
test_apk = ROOT / 'app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk'
adb('install', '-r', str(apk))
adb('install', '-r', '-t', str(test_apk))
adb('logcat', '-c')

classes = ','.join([
    'com.abosultan.darbakos.PositionStateTest',
    'com.abosultan.darbakos.P4GpsTripTest',
    'com.abosultan.darbakos.OsmAndBridgeTest',
    'com.abosultan.darbakos.TripPersistenceTest'
])
focused = subprocess.check_output(ADB + ['shell', 'am', 'instrument', '-w', '-e', 'class', classes,
    PACKAGE + '.test/androidx.test.runner.AndroidJUnitRunner'], text=True, timeout=180)
save('p4-focused-instrumentation.txt', focused)
assert 'OK (9 tests)' in focused and 'FAILURES' not in focused, focused

# Verify the real Android LocationManager path reaches the final Home speed surface.
adb('shell', 'pm', 'grant', PACKAGE, 'android.permission.ACCESS_FINE_LOCATION')
adb('shell', 'am', 'force-stop', PACKAGE)
launch = adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
save('p4-gps-launch.txt', launch)
assert 'Status: ok' in launch, launch
time.sleep(1)
subprocess.check_call(ADB + ['emu', 'geo', 'fix', '46.6753', '24.7136'])

tree = None
for _ in range(12):
    time.sleep(0.5)
    tree = dump()
    if node(tree, 'speed_source').get('text') == 'GPS • مباشر':
        break
assert node(tree, 'speed_source').get('text') == 'GPS • مباشر'
speed_text = node(tree, 'speed_value').get('text')
assert re.fullmatch(r'\d+', speed_text), speed_text
assert node(tree, 'navigation_instruction').get('text') == 'لا يوجد مسار نشط'
assert node(tree, 'media_state').get('text') == 'متوقف'

png = adb('exec-out', 'screencap', '-p', binary=True)
assert png[:8] == b'\x89PNG\r\n\x1a\n'
save('p4-gps-live-1024x600.png', png)
crash = adb('logcat', '-b', 'crash', '-d')
logs = adb('logcat', '-d')
save('p4-gps-crash.txt', crash)
save('p4-gps-logcat.txt', logs)
assert 'FATAL EXCEPTION' not in crash
assert 'ANR in ' + PACKAGE not in logs
save('p4-focused-summary.json', json.dumps({
    'result': 'PASS', 'api': 25, 'resolution': '1024x600',
    'focused_tests': 9, 'gps_permission_granted_by_test': True,
    'emulator_geo_fix_reached_home': True, 'speed_text': speed_text,
    'osmand_bridge_absent_fallback_tested': True,
    'trip_persistence_round_trip_tested': True,
    'trip_partial_file_rejected': True,
    'trip_duplicate_name_safe': True,
    'storage_preference_tested': True,
    'runtime_ownership_policy_tested': True,
    'full_regression_runs': 0, 'guardian_suite_runs': 0, 't3_validated': False
}, ensure_ascii=False, indent=2))
print('PASS: P4 GPS/Trip persistence/OsmAnd bridge focused tests and live LocationManager -> Home speed UI')
