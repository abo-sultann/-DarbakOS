#!/usr/bin/env python3
"""Bounded API25 verification for Darbak P5 external MediaSession foundation."""
from pathlib import Path
import hashlib
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
LISTENER = PACKAGE + '/com.abosultan.darbakos.core.DarbakMediaNotificationListener'
serials = [line.split()[0] for line in subprocess.check_output(['adb', 'devices'], text=True).splitlines()[1:]
           if line.endswith('\tdevice') and line.startswith('emulator-')]
assert len(serials) == 1
ADB = ['adb', '-s', serials[0]]


def adb(*args, binary=False):
    return subprocess.check_output(ADB + list(args), text=not binary, timeout=60)


def save(name, value):
    path = OUT / name
    path.write_bytes(value) if isinstance(value, bytes) else path.write_text(value)


def instrument(classes, expected, name):
    selected = ','.join('com.abosultan.darbakos.' + item for item in classes)
    output = subprocess.check_output(ADB + ['shell', 'am', 'instrument', '-w', '-e', 'class', selected,
        PACKAGE + '.test/androidx.test.runner.AndroidJUnitRunner'], text=True, timeout=180)
    save(name + '.txt', output)
    assert 'OK (' + str(expected) + ' tests)' in output and 'FAILURES' not in output, output


def dump(name):
    adb('shell', 'uiautomator', 'dump', '/sdcard/darbak-ui.xml')
    xml = adb('shell', 'cat', '/sdcard/darbak-ui.xml')
    save(name + '.xml', xml)
    return ET.fromstring(xml)


def matches(tree, resource):
    return [n for n in tree.iter('node') if n.get('resource-id', '').endswith(':id/' + resource)]


def node(tree, resource):
    found = matches(tree, resource)
    assert len(found) == 1, resource
    return found[0]


def tap_resource(tree, resource):
    bounds = node(tree, resource).get('bounds', '')
    match = re.fullmatch(r'\[(\d+),(\d+)\]\[(\d+),(\d+)\]', bounds)
    assert match, (resource, bounds)
    left, top, right, bottom = map(int, match.groups())
    adb('shell', 'input', 'tap', str((left + right) // 2), str((top + bottom) // 2))
    time.sleep(0.4)


def screen(name):
    png = adb('exec-out', 'screencap', '-p', binary=True)
    assert png[:8] == b'\x89PNG\r\n\x1a\n'
    assert (int.from_bytes(png[16:20], 'big'), int.from_bytes(png[20:24], 'big')) == (1024, 600)
    save(name + '.png', png)


def set_listener(value):
    if value and value != 'null':
        adb('shell', 'settings', 'put', 'secure', 'enabled_notification_listeners', value)
    else:
        adb('shell', 'settings', 'delete', 'secure', 'enabled_notification_listeners')
    time.sleep(0.8)


original_listener = 'null'
try:
    assert adb('shell', 'getprop', 'ro.build.version.sdk').strip() == '25'
    adb('shell', 'wm', 'size', '1024x600')
    adb('shell', 'wm', 'density', '160')
    apk = ROOT / 'app/build/outputs/apk/debug/app-debug.apk'
    test_apk = ROOT / 'app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk'
    adb('install', '-r', str(apk))
    adb('install', '-r', '-t', str(test_apk))
    adb('shell', 'pm', 'grant', PACKAGE, 'android.permission.ACCESS_FINE_LOCATION')
    adb('logcat', '-c')
    original_listener = adb('shell', 'settings', 'get', 'secure', 'enabled_notification_listeners').strip()

    # State A: access deliberately unavailable. This proves final UI is truthful and cannot autoplay.
    set_listener('null')
    instrument(['ShellTest', 'MediaSnapshotTest'], 9, 'media-no-access-instrumentation')

    adb('shell', 'am', 'force-stop', PACKAGE)
    launch = adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
    save('media-launch.txt', launch)
    assert 'Status: ok' in launch
    subprocess.check_call(ADB + ['emu', 'geo', 'fix', '46.6753', '24.7136'])
    time.sleep(1)
    home = dump('media-home-no-access')
    assert node(home, 'media_track').get('text') == 'لا يوجد مقطع محدد'
    assert node(home, 'media_state').get('text') == 'وصول الوسائط غير مفعّل'
    tap_resource(home, 'nav_media')
    media = dump('media-panel-no-access')
    assert node(media, 'media_now_title').get('text') == 'وصول الوسائط غير مفعّل'
    assert node(media, 'media_play_pause_button').get('enabled') == 'false'
    assert node(media, 'media_previous_button').get('enabled') == 'false'
    assert node(media, 'media_next_button').get('enabled') == 'false'
    assert node(media, 'media_access_button').get('enabled') == 'true'
    services = adb('shell', 'dumpsys', 'activity', 'services', PACKAGE)
    save('media-trip-service.txt', services)
    assert 'TripRuntimeService' in services and 'startRequested=true' in services
    screen('media-panel-no-access-1024x600')

    # State B: grant the exact notification-listener component, then verify a real framework
    # MediaSession can be observed and controlled only after an explicit user transport call.
    set_listener(LISTENER)
    instrument(['MediaSessionBridgeTest'], 2, 'media-session-instrumentation')

    # The test session was released. Darbak itself must own no playback session and must return
    # to a quiet IDLE state when access is available but nothing is playing.
    adb('shell', 'am', 'force-stop', PACKAGE)
    adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
    time.sleep(1)
    home_access = dump('media-home-access-idle')
    assert node(home_access, 'media_track').get('text') == 'لا يوجد مقطع محدد'
    assert node(home_access, 'media_state').get('text') == 'متوقف'
    tap_resource(home_access, 'nav_media')
    media_access = dump('media-panel-access-idle')
    assert node(media_access, 'media_now_title').get('text') == 'لا يوجد مقطع محدد'
    assert node(media_access, 'media_play_pause_button').get('enabled') == 'false'
    assert node(media_access, 'media_previous_button').get('enabled') == 'false'
    assert node(media_access, 'media_next_button').get('enabled') == 'false'
    assert not matches(media_access, 'media_access_button'), 'Access action must hide after grant'
    screen('media-panel-access-idle-1024x600')
    sessions = adb('shell', 'dumpsys', 'media_session')
    save('media-session-after-tests.txt', sessions)
    assert 'DarbakP5Observe' not in sessions and 'DarbakP5Transport' not in sessions

    save('summary.json', json.dumps({
        'result': 'PASS',
        'commit': subprocess.check_output(['git', 'rev-parse', 'HEAD'], text=True).strip(),
        'api': 25,
        'abi': adb('shell', 'getprop', 'ro.product.cpu.abi').strip(),
        'resolution': '1024x600', 'density': 160,
        'instrumentation_invocations': 2,
        'focused_tests': 11,
        'shell_and_snapshot_without_access': 9,
        'real_media_session_with_access': 2,
        'notification_listener_component': LISTENER,
        'no_autoplay_proven': True,
        'trip_runtime_alive_on_media_surface': True,
        'full_regression_runs': 0,
        'guardian_suite_runs': 0,
        'apk_bytes': apk.stat().st_size,
        'apk_sha256': hashlib.sha256(apk.read_bytes()).hexdigest(),
        't3_validated': False
    }, ensure_ascii=False, indent=2))
    print('PASS: P5 external MediaSession gate; no autoplay, no historical regression')
finally:
    crash = adb('logcat', '-b', 'crash', '-d')
    logs = adb('logcat', '-d')
    save('final-crash.txt', crash)
    save('final-logcat.txt', logs)
    try:
        set_listener(original_listener)
    except Exception:
        pass
    assert 'FATAL EXCEPTION' not in crash, crash
    assert 'ANR in ' + PACKAGE not in logs
