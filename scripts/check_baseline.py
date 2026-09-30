#!/usr/bin/env python3
"""Offline source guard for current Darbak OS constraints; not a substitute for Android tests."""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
A = '{http://schemas.android.com/apk/res/android}'
main = ROOT / 'app/src/main'
for path in main.rglob('*.xml'):
    ET.parse(path)
manifest = ET.parse(main / 'AndroidManifest.xml').getroot()
app = manifest.find('application')
assert app.get(A + 'supportsRtl') == 'true'
permissions = {item.get(A + 'name') for item in manifest.findall('uses-permission')}
assert permissions == {'android.permission.ACCESS_FINE_LOCATION'}, \
    'P4 may request only fine location at this checkpoint'
services = app.findall('service')
assert len(services) == 1 and services[0].get(A + 'name') == '.core.TripRuntimeService'
assert services[0].get(A + 'exported') == 'false' and not services[0].findall('intent-filter')
assert services[0].get(A + 'process') is None and not app.findall('receiver'), \
    'Only the approved private in-process Trip runtime Service is allowed'
activity = app.find('activity')
assert activity.get(A + 'screenOrientation') == 'landscape'
assert all(c.get(A + 'name') != 'android.intent.category.HOME'
           for c in activity.findall('.//category')), 'P4 must not take over the device launcher yet'
build = (ROOT / 'app/build.gradle').read_text()
assert re.search(r'minSdk\s+25\b', build)
assert not re.search(r'^\s*(?:implementation|api|runtimeOnly)\b', build, re.M)
assert not list(main.rglob('*.so')), 'No native ABI dependency expected'
strings = ET.parse(main / 'res/values/strings.xml').getroot()
assert all(not re.search(r'TEST|experimental|preview|prototype|تجريب|معاينة|اختبار', item.text or '', re.I)
           for item in strings), 'No temporary user-facing copy'
java = '\n'.join(p.read_text() for p in main.rglob('*.java'))
assert 'MediaPlayer' not in java and 'BluetoothAdapter' not in java
assert 'LocationManager' in java, 'P4 GPS source must remain explicit and reviewable'
runtime = main / 'java/com/abosultan/darbakos/core/TripRuntimeService.java'
assert runtime.read_text().count('new HandlerThread(') == 1
assert runtime.read_text().count('new Handler(') == 1
for path in main.rglob('*.java'):
    source = path.read_text()
    assert not re.search(r'new\s+(?:Thread|Timer)\s*\(|ExecutorService|Executors\.', source), path
    if path != runtime:
        assert not re.search(r'new\s+(?:HandlerThread|Handler)\s*\(', source), path
print('PASS: API25/RTL, fine-location only, one private Trip Service/worker, no extra worker/receiver/runtime dependency/native code')
