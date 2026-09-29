#!/usr/bin/env python3
"""API25 emulator-only smoke; UI taps always come from UIAutomator bounds."""
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
PACKAGE = 'com.abosultan.darbakos.test'
ACTIVITY = PACKAGE + '/com.abosultan.darbakos.MainActivity'
assert sys.argv[1:] in ([], ['--diagnostics-only']), 'Unknown verification scope'
DIAGNOSTICS_ONLY = sys.argv[1:] == ['--diagnostics-only']
devices = subprocess.check_output(['adb', 'devices'], text=True).splitlines()[1:]
serials = [line.split()[0] for line in devices if line.endswith('\tdevice') and line.startswith('emulator-')]
assert len(serials) == 1, 'Run with exactly one emulator; this script never targets physical devices'
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
    tree = ET.fromstring(xml)
    for item in tree.iter('node'):
        assert not re.search(r'TEST|experimental|preview|prototype|تجريب|معاينة|اختبار',
                             item.get('text', '') + item.get('content-desc', ''), re.I), item.attrib
    return tree


def node(tree, resource):
    nodes = [n for n in tree.iter('node') if n.get('resource-id', '').endswith(':id/' + resource)]
    assert len(nodes) == 1, 'Missing or ambiguous UI element: ' + resource
    return nodes[0]


def tap(tree, resource):
    target = node(tree, resource)
    assert target.get('enabled') == 'true' and target.get('clickable') == 'true', resource
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', target.get('bounds')))
    adb('shell', 'input', 'tap', str((x1+x2)//2), str((y1+y2)//2))


def assert_home_states(tree):
    assert node(tree, 'vehicle_summary').get('text') == 'حالة السيارة'
    assert node(tree, 'speed_value').get('text') == '—'
    assert 'غير متاحة' in node(tree, 'navigation_state').get('text', '')
    assert node(tree, 'media_track').get('text') == 'لا يوجد مقطع محدد'
    assert node(tree, 'media_state').get('text') == 'متوقف'
    assert node(tree, 'navigation_instruction').get('text') == 'لا يوجد مسار نشط'
    assert node(tree, 'navigation_eta').get('text') == 'اختر وجهة لبدء الملاحة'
    assert node(tree, 'vehicle_state').get('text') == 'بيانات السيارة غير متاحة'
    assert node(tree, 'test_badge').get('text') == 'دربك OS'
    assert not any(n.get('resource-id', '').endswith(':id/apps_preview') for n in tree.iter('node'))
    assert node(tree, 'nav_home').get('selected') == 'true'


def screenshot(name):
    png = adb('exec-out', 'screencap', '-p', binary=True)
    assert png[:8] == b'\x89PNG\r\n\x1a\n'
    assert int.from_bytes(png[16:20], 'big') == 1024
    assert int.from_bytes(png[20:24], 'big') == 600
    save(name + '.png', png)


try:
    assert adb('shell', 'getprop', 'ro.build.version.sdk').strip() == '25'
    adb('shell', 'wm', 'size', '1024x600')
    adb('shell', 'wm', 'density', '160')
    adb('shell', 'settings', 'put', 'system', 'accelerometer_rotation', '0')
    adb('shell', 'settings', 'put', 'system', 'user_rotation', '0')
    for setting in ('window_animation_scale', 'transition_animation_scale', 'animator_duration_scale'):
        adb('shell', 'settings', 'put', 'global', setting, '0')
    apk = ROOT / 'app/build/outputs/apk/debug/app-debug.apk'
    test_apk = ROOT / 'app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk'
    adb('install', '-r', str(apk))
    adb('install', '-r', '-t', str(test_apk))
    adb('logcat', '-c')
    if DIAGNOSTICS_ONLY:
        # This gate explicitly authorizes only this class; exit before all regression/UI work.
        focused_class = 'com.abosultan.darbakos.GuardianDiagnosticRecordTest'
        focused = subprocess.check_output(ADB + ['shell', 'am', 'instrument', '-w', '-e',
            'class', focused_class, PACKAGE + '.test/androidx.test.runner.AndroidJUnitRunner'],
            text=True, timeout=180)
        save('focused-instrumentation.txt', focused)
        assert 'OK (2 tests)' in focused and 'FAILURES' not in focused, focused
        crash = adb('logcat', '-b', 'crash', '-d')
        logs = adb('logcat', '-d')
        save('crash.txt', crash)
        save('logcat.txt', logs)
        assert 'FATAL EXCEPTION' not in crash, crash
        assert 'ANR in ' + PACKAGE not in logs
        save('summary.json', json.dumps({
            'result': 'PASS', 'scope': 'GuardianDiagnosticRecordTest only',
            'commit': subprocess.check_output(['git', 'rev-parse', 'HEAD'], text=True).strip(),
            'api': 25, 'abi': adb('shell', 'getprop', 'ro.product.cpu.abi').strip(),
            'resolution': '1024x600', 'density': 160, 'focused_instrumented_tests': 2,
            'focused_class': focused_class, 'regression_runs': 0, 'giant_suite_runs': 0,
            'ui_smoke_run': False,
            'apk_bytes': apk.stat().st_size, 'apk_sha256': hashlib.sha256(apk.read_bytes()).hexdigest(),
            't3_validated': False
        }, indent=2))
        print('PASS: GuardianDiagnosticRecordTest only; no regression or UI smoke run')
        raise SystemExit(0)
    # Consolidated gate: focused Android checks must pass before the ONE regression.
    focused_class = 'com.abosultan.darbakos.GuardianMonitorSessionTest'
    focused = subprocess.check_output(ADB + ['shell', 'am', 'instrument', '-w', '-e',
        'class', focused_class, PACKAGE + '.test/androidx.test.runner.AndroidJUnitRunner'],
        text=True, timeout=180)
    save('focused-instrumentation.txt', focused)
    assert 'OK (10 tests)' in focused and 'FAILURES' not in focused, focused
    # Owner explicitly requested reuse of unchanged giant-proof evidence.
    # Preserve those tests in source; this gate alone selects the remaining regression.
    reused = {
        'GuardianPolicyTest#all1024CombinationsRespectPrecedenceWithoutMutation',
        'GuardianAssessmentTest#all1024CombinationsHaveExactExclusiveMembershipCountsAndOverall',
        'GuardianRecoveryPolicyTest#all6144CombinationLevelsAreDeterministicAndNonMutating',
        'GuardianSnapshotTest#all1024CombinationsCaptureExactStatesAndCorrectAggregate',
        'GuardianSnapshotTest#concurrentUpdatesAndResetCannotSplitSnapshotOrAggregate',
        'GuardianSupervisorTest#all6144CombinationLevelsHaveExactChainAndDoNotMutateRegistry',
        'GuardianSupervisorTest#concurrentCallersUpdatesAndResetCannotSplitAnyResult',
    }
    selected = []
    focused_methods = []
    discovered = set()
    basis = json.loads((ROOT / 'scripts/guardian_reused_proofs.json').read_text())
    for source, expected_hash in basis['source_sha256'].items():
        assert hashlib.sha256((ROOT / source).read_bytes()).hexdigest() == expected_hash, \
            'Prior proof invalidated by source change: ' + source
    for source in sorted((ROOT / 'app/src/androidTest/java/com/abosultan/darbakos').glob('*Test.java')):
        for method in re.findall(r'@Test\s+public\s+void\s+(\w+)\s*\(', source.read_text()):
            name = source.stem + '#' + method
            discovered.add(name)
            if source.stem == 'GuardianMonitorSessionTest':
                focused_methods.append('com.abosultan.darbakos.' + name)
            elif name not in reused:
                selected.append('com.abosultan.darbakos.' + name)
    assert reused <= discovered and len(discovered) == 91 and len(selected) == 74 and len(focused_methods) == 10
    save('regression-selection.json', json.dumps({
        'selected': selected, 'reused_unchanged_proofs': sorted(reused),
        'already_passed_focused_session_tests': focused_methods,
        'prior_evidence': basis['evidence'],
        'prior_tested_commit': basis['tested_commit'],
        'unchanged_source_sha256': basis['source_sha256'],
        'scope': 'One bounded regression: 58 prior +16 foundation tests; 10 session tests already passed; 7 giant proofs reused'
    }, indent=2))
    # Every selected Android assertion runs on the real framework, not a mocked JVM.
    result = subprocess.check_output(ADB + ['shell', 'am', 'instrument', '-w',
        '-e', 'class', ','.join(selected),
        PACKAGE + '.test/androidx.test.runner.AndroidJUnitRunner'], text=True, timeout=180)
    save('instrumentation.txt', result)
    assert 'OK (74 tests)' in result and 'FAILURES' not in result, result
    adb('shell', 'am', 'force-stop', PACKAGE)
    launch = adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
    save('launch.txt', launch)
    assert 'Status: ok' in launch, launch
    tree = dump('home')
    assert any(n.get('resource-id', '').endswith(':id/speed_value') for n in tree.iter('node'))
    screenshot('home-1024x600')
    assert_home_states(tree)
    quick_results = []
    for section, title in (('map', 'الخريطة'), ('media', 'الوسائط'), ('vehicle', 'السيارة')):
        button = node(tree, 'quick_' + section)
        x1, y1, x2, y2 = map(int, re.findall(r'\d+', button.get('bounds')))
        assert 0 <= x1 < x2 <= 1024 and 0 <= y1 < y2 <= 600
        assert x2 - x1 >= 56 and y2 - y1 >= 56, 'Quick-action touch target too small'
        for back_method in ('system_back', 'back_home'):
            tap(tree, 'quick_' + section)
            destination = dump('quick-' + section + '-' + back_method)
            assert node(destination, 'section_title').get('text') == title
            assert node(destination, 'nav_' + section).get('selected') == 'true'
            if back_method == 'system_back':
                adb('shell', 'input', 'keyevent', '4')
            else:
                tap(destination, 'back_home')
            tree = dump('quick-' + section + '-' + back_method + '-returned')
            assert_home_states(tree)
            quick_results.append({'button': 'quick_' + section, 'destination': title,
                                  'return': back_method, 'bounds': button.get('bounds'), 'result': 'PASS'})
    save('quick-actions.json', json.dumps(quick_results, ensure_ascii=False, indent=2))
    for section in ('map', 'media', 'vehicle', 'apps'):
        tap(tree, 'nav_' + section)
        tree = dump(section)
        assert any(n.get('resource-id', '').endswith(':id/section_title') for n in tree.iter('node'))
        if section == 'apps':
            for resource, label in (('apps_recent', 'الأخيرة'), ('apps_favorite', 'المفضلة'), ('apps_manage', 'إدارة التطبيقات')):
                target = node(tree, resource)
                assert target.get('text') == label and target.get('enabled') == 'false'
                x1, y1, x2, y2 = map(int, re.findall(r'\d+', target.get('bounds')))
                assert 0 <= x1 < x2 <= 1024 and 0 <= y1 < y2 <= 600 and y2-y1 >= 56
        else:
            assert not any(n.get('resource-id', '').endswith(':id/apps_preview') for n in tree.iter('node'))
        screenshot(section + '-1024x600')
    tap(tree, 'settings_button')
    tree = dump('settings')
    screenshot('settings-1024x600')
    # A force-stopped process must reopen Home, even when last stopped in Settings.
    adb('shell', 'am', 'force-stop', PACKAGE)
    adb('shell', 'am', 'start', '-W', '-n', ACTIVITY)
    tree = dump('cold-restart-home')
    assert_home_states(tree)
    # Check again after settling: the static preview must not progress or start a session/service.
    time.sleep(2)
    tree = dump('cold-restart-home-settled')
    assert_home_states(tree)
    screenshot('cold-restart-home-1024x600')
    services = adb('shell', 'dumpsys', 'activity', 'services', PACKAGE)
    sessions = adb('shell', 'dumpsys', 'media_session')
    save('cold-restart-services.txt', services)
    save('cold-restart-media-session.txt', sessions)
    save('cold-restart-audio.txt', adb('shell', 'dumpsys', 'audio'))
    assert 'ServiceRecord{' not in services, services
    assert PACKAGE not in sessions, sessions
    save('no-autoplay.json', json.dumps({
        'result': 'PASS', 'home_stopped_unavailable_media_after_restart': True,
        'no_track_or_fabricated_position_after_settle': True, 'app_service_records': 0,
        'app_media_sessions': 0,
        'scope': 'Production-facing shell without playback code; not a future playback-engine test'
    }, indent=2))
    save('meminfo.txt', adb('shell', 'dumpsys', 'meminfo', PACKAGE))
    time.sleep(2)
    save('cpuinfo.txt', adb('shell', 'dumpsys', 'cpuinfo'))
    save('gfxinfo.txt', adb('shell', 'dumpsys', 'gfxinfo', PACKAGE))
    crash = adb('logcat', '-b', 'crash', '-d')
    save('crash.txt', crash)
    assert 'FATAL EXCEPTION' not in crash, crash
    logs = adb('logcat', '-d')
    save('logcat.txt', logs)
    assert 'ANR in ' + PACKAGE not in logs
    save('summary.json', json.dumps({
        'result': 'PASS', 'commit': subprocess.check_output(['git', 'rev-parse', 'HEAD'], text=True).strip(),
        'api': 25, 'abi': adb('shell', 'getprop', 'ro.product.cpu.abi').strip(),
        'resolution': '1024x600', 'density': 160, 'instrumented_tests': 74,
        'focused_instrumented_tests': 10, 'reused_unchanged_proofs': 7,
        'bounded_regression_runs': 1,
        'quick_action_round_trips': len(quick_results),
        'apk_bytes': apk.stat().st_size, 'apk_sha256': hashlib.sha256(apk.read_bytes()).hexdigest(),
        't3_validated': False
    }, indent=2))
    print('PASS: API25 instrumentation, navigation, RTL/fit, cold restart, screenshots and crash checks')
finally:
    # Preserve diagnostics even when an assertion fails.
    for name, args in [('final-logcat.txt', ('logcat', '-d')),
                       ('final-crash.txt', ('logcat', '-b', 'crash', '-d'))]:
        try:
            save(name, adb(*args))
        except (subprocess.SubprocessError, OSError):
            pass
