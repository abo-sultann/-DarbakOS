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
assert not app.findall('service') and not app.findall('receiver'), \
    'No background Service/Receiver approved yet'
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
assert not any(token in java for token in ('Thread(', 'Timer(', 'Handler(', 'ExecutorService')), \
    'No production worker/scheduler approved in this checkpoint'
print('PASS: XML, minSdk25, RTL, landscape, fine-location-only P4 GPS, no services/runtime dependencies/native code')
