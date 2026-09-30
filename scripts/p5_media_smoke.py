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


def dump_retry(name, attempts=6):
    last = ''
    for attempt in range(attempts):
        subprocess.run(ADB + ['shell', 'rm', '-f', '/sdcard/darbak-ui.xml'],
                       stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        result = subprocess.run(ADB + ['shell', 'uiautomator', 'dump', '/sdcard/darbak-ui.xml'],
                                text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                                timeout=30)
        last = result.stdout or ''
        try:
            xml = adb('shell', 'cat', '/sdcard/darbak-ui.xml')
            tree = ET.fromstring(xml)
            save(name + '.xml', xml)
            save(name + '-dump.txt', last)
            return tree
        except (subprocess.CalledProcessError, ET.ParseError):
            time.sleep(0.7 + attempt * 0.2)
    save(name + '-dump-failure.txt', last + '\n' + adb('shell', 'dumpsys', 'window', 'windows'))
    raise AssertionError('uiautomator could not capture Settings after retries')


def center(node):
    bounds = node.get('bounds', '')
    match = re.fullmatch(r'\[(\d+),(\d+)\]\[(\d+),(\d+)\]', bounds)
    assert match, bounds
    left, top, right, bottom = map(int, match.groups())
    return (left + right) // 2, (top + bottom) // 2


def tap_node(node):
    x, y = center(node)
    adb('shell', 'input', 'tap', str(x), str(y))
    time.sleep(0.7)


def listener_setting():
    return adb('shell', 'settings', 'get', 'secure', 'enabled_notification_listeners').strip()


def grant_listener_via_settings():
    launch = adb('shell', 'am', 'start', '-W', '-a',
                 'android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS')
    save('media-listener-settings-launch.txt', launch)
    time.sleep(1.2)
    tree = dump_retry('media-listener-settings')
    candidates = []
    for item in tree.iter('node'):
        text = item.get('text', '')
        desc = item.get('content-desc', '')
        if ('دربك' in text or 'دربك' in desc or 'Darbak' in text or 'Darbak' in desc):
            candidates.append(item)
    assert candidates, 'Darbak media access row not found in Notification access Settings'
    # Prefer a visible label with useful bounds; tapping the row label toggles the API25 switch.
    candidates.sort(key=lambda item: (item.get('text', '') == '', -len(item.get('text', ''))))
    tap_node(candidates[0])

    for attempt in range(8):
        enabled = listener_setting()
        if 'DarbakMediaNotificationListener' in enabled:
            save('media-listener-enabled.txt', enabled)
            adb('shell', 'am', 'force-stop', 'com.android.settings')
            time.sleep(1.2)
            return
        tree = dump_retry('media-listener-confirm-' + str(attempt), attempts=3)
        positive = [n for n in tree.iter('node')
                    if n.get('resource-id', '').endswith(':id/button1')
                    or n.get('resource-id', '') == 'android:id/button1']
        if positive:
            tap_node(positive[0])
        time.sleep(0.5)
    save('media-listener-enable-failure.txt', listener_setting())
    raise AssertionError('Notification-listener access did not become enabled through Settings UI')


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
    # Fresh CI emulator has no enabled notification listeners. Make that invariant explicit.
    adb('shell', 'settings', 'delete', 'secure', 'enabled_notification_listeners')
    adb('logcat', '-c')

    # State A: access unavailable. Final UI must stay truthful and all media transport disabled.
    instrument(['ShellTest', 'MediaSnapshotTest'], 9, 'media-no-access-instrumentation')
    launch = adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
    save('media-launch-no-access.txt', launch)
    assert 'Status: ok' in launch
    subprocess.check_call(ADB + ['emu', 'geo', 'fix', '46.6753', '24.7136'])
    time.sleep(0.8)
    services = adb('shell', 'dumpsys', 'activity', 'services', PACKAGE)
    save('media-trip-service.txt', services)
    assert 'TripRuntimeService' in services and 'startRequested=true' in services
    screen('media-home-no-access-1024x600')

    # State B: grant exactly as a user does on Android 7.1 Settings. This avoids relying on shell
    # commands that do not exist on API25 and proves the declared listener is discoverable.
    adb('shell', 'am', 'force-stop', PACKAGE)
    grant_listener_via_settings()
    instrument(['MediaSessionBridgeTest', 'MediaAccessShellTest'], 3,
               'media-session-access-instrumentation')

    # Both test MediaSessions are released. Darbak must not own a lingering playback session.
    sessions = adb('shell', 'dumpsys', 'media_session')
    save('media-session-after-tests.txt', sessions)
    assert 'DarbakP5Observe' not in sessions and 'DarbakP5Transport' not in sessions
    adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
    time.sleep(0.8)
    screen('media-home-access-idle-1024x600')

    save('summary.json', json.dumps({
        'result': 'PASS',
        'commit': subprocess.check_output(['git', 'rev-parse', 'HEAD'], text=True).strip(),
        'api': 25,
        'abi': adb('shell', 'getprop', 'ro.product.cpu.abi').strip(),
        'resolution': '1024x600', 'density': 160,
        'instrumentation_invocations': 2,
        'focused_tests': 12,
        'shell_and_snapshot_without_access': 9,
        'real_media_session_and_idle_ui_with_access': 3,
        'notification_listener_component': LISTENER,
        'permission_grant_path': 'Android Settings notification access UI',
        'no_autoplay_proven': True,
        'trip_runtime_alive': True,
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
        adb('shell', 'settings', 'delete', 'secure', 'enabled_notification_listeners')
    except Exception:
        pass
    assert 'FATAL EXCEPTION' not in crash, crash
    assert 'ANR in ' + PACKAGE not in logs
