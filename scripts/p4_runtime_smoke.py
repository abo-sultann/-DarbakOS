#!/usr/bin/env python3
"""Adapt the existing P4 GPS smoke to the bounded continuous-runtime verification gate."""
from pathlib import Path
import hashlib
import json
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'test-evidence'
OUT.mkdir(exist_ok=True)
assert sys.argv[1:] in (['--runtime-probe'], ['--runtime-gate'])
PROBE = sys.argv[1:] == ['--runtime-probe']
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


def dump(name):
    adb('shell', 'uiautomator', 'dump', '/sdcard/darbak-ui.xml')
    xml = adb('shell', 'cat', '/sdcard/darbak-ui.xml')
    save(name + '.xml', xml)
    return ET.fromstring(xml)


def node(tree, resource):
    found = [n for n in tree.iter('node') if n.get('resource-id', '').endswith(':id/' + resource)]
    assert len(found) == 1, resource
    return found[0]


def wait_source(text, name):
    for _ in range(12):
        tree = dump(name)
        if node(tree, 'speed_source').get('text') == text:
            return tree
        time.sleep(0.5)
    raise AssertionError('Home source did not become ' + text)


def screen(name):
    png = adb('exec-out', 'screencap', '-p', binary=True)
    assert png[:8] == b'\x89PNG\r\n\x1a\n'
    assert (int.from_bytes(png[16:20], 'big'), int.from_bytes(png[20:24], 'big')) == (1024, 600)
    save(name + '.png', png)


try:
    assert adb('shell', 'getprop', 'ro.build.version.sdk').strip() == '25'
    adb('shell', 'wm', 'size', '1024x600')
    adb('shell', 'wm', 'density', '160')
    apk = ROOT / 'app/build/outputs/apk/debug/app-debug.apk'
    test_apk = ROOT / 'app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk'
    adb('install', '-r', str(apk))
    adb('install', '-r', '-t', str(test_apk))
    adb('shell', 'pm', 'grant', PACKAGE, 'android.permission.ACCESS_FINE_LOCATION')
    adb('shell', 'appops', 'set', PACKAGE, 'android:mock_location', 'allow')
    adb('logcat', '-c')
    names = ['core.TripAutoRecorderTest', 'TripRuntimeTest']
    if not PROBE:
        names = ['PositionStateTest', 'P4GpsTripTest', 'TripRecorderTest',
                 'TripPersistenceTest', 'OsmAndBridgeTest'] + names
    expected = 11 if PROBE else 26
    classes = ','.join('com.abosultan.darbakos.' + name for name in names)
    save('selection.json', json.dumps({'classes': names, 'expected_tests': expected,
         'scope': 'defect reproduction only' if PROBE else 'one consolidated P4 runtime gate',
         'full_regression_runs': 0, 'guardian_suite_runs': 0}, indent=2))
    focused = subprocess.check_output(ADB + ['shell', 'am', 'instrument', '-w', '-e', 'class', classes,
        PACKAGE + '.test/androidx.test.runner.AndroidJUnitRunner'], text=True, timeout=180)
    save('focused-instrumentation.txt', focused)
    assert 'OK (' + str(expected) + ' tests)' in focused and 'FAILURES' not in focused, focused
    if PROBE:
        print('PASS: runtime defect probe only')
        raise SystemExit(0)

    # Use the real emulator GPS provider after removing the instrumentation-only mock permission.
    adb('shell', 'appops', 'set', PACKAGE, 'android:mock_location', 'deny')
    adb('shell', 'am', 'force-stop', PACKAGE)
    launch = adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
    save('launch.txt', launch)
    assert 'Status: ok' in launch
    time.sleep(1)
    subprocess.check_call(ADB + ['emu', 'geo', 'fix', '46.6753', '24.7136'])
    tree = wait_source('GPS • مباشر', 'home-live')
    assert re.fullmatch(r'\d+', node(tree, 'speed_value').get('text'))
    assert node(tree, 'navigation_instruction').get('text') == 'لا يوجد مسار نشط'
    assert node(tree, 'media_state').get('text') == 'متوقف'
    screen('home-live-1024x600')
    pid = adb('shell', 'pidof', PACKAGE).strip()
    save('location-home.txt', adb('shell', 'dumpsys', 'location'))
    # External Settings is a controlled foreground app; installed OsmAnd UI is not assumed.
    adb('shell', 'am', 'start', '-W', '-a', 'android.settings.SETTINGS')
    services = adb('shell', 'dumpsys', 'activity', 'services', PACKAGE)
    save('services-background.txt', services)
    assert 'TripRuntimeService' in services and 'startRequested=true' in services
    assert adb('shell', 'pidof', PACKAGE).strip() == pid
    subprocess.check_call(ADB + ['emu', 'geo', 'fix', '46.6754', '24.7137'])
    time.sleep(1)
    save('location-background.txt', adb('shell', 'dumpsys', 'location'))
    adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
    tree = wait_source('GPS • مباشر', 'home-returned')
    assert adb('shell', 'pidof', PACKAGE).strip() == pid
    screen('home-returned-1024x600')
    save('meminfo.txt', adb('shell', 'dumpsys', 'meminfo', PACKAGE))
    adb('shell', 'settings', 'put', 'secure', 'location_providers_allowed', '-gps')
    tree = wait_source('GPS • غير متاح', 'home-gps-unavailable')
    assert node(tree, 'speed_value').get('text') == '—'
    save('summary.json', json.dumps({
        'result': 'PASS', 'commit': subprocess.check_output(['git', 'rev-parse', 'HEAD'], text=True).strip(),
        'api': 25, 'abi': adb('shell', 'getprop', 'ro.product.cpu.abi').strip(),
        'resolution': '1024x600', 'density': 160, 'focused_tests': expected, 'classes': names,
        'consolidated_gate_runs': 1, 'full_regression_runs': 0, 'guardian_suite_runs': 0,
        'gps_home_and_background_return': True, 'provider_disabled_truthful': True,
        'background_app': 'Android Settings; no installed OsmAnd UI claim',
        'instrumentation_location_fixtures': 'test-only mock provider; actual LocationManager callbacks',
        'apk_bytes': apk.stat().st_size, 'apk_sha256': hashlib.sha256(apk.read_bytes()).hexdigest(),
        't3_validated': False
    }, ensure_ascii=False, indent=2))
    print('PASS: focused P4 runtime gate; no historical regression or Guardian suites')
finally:
    crash = adb('logcat', '-b', 'crash', '-d')
    logs = adb('logcat', '-d')
    save('final-crash.txt', crash)
    save('final-logcat.txt', logs)
    adb('shell', 'settings', 'put', 'secure', 'location_providers_allowed', '+gps')
    assert 'FATAL EXCEPTION' not in crash, crash
    assert 'ANR in ' + PACKAGE not in logs
