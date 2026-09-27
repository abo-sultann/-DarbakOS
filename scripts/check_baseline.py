#!/usr/bin/env python3
"""Offline source guard for P1 constraints; not a substitute for Android tests."""
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
assert not manifest.findall('uses-permission'), 'P1 must not request device/network permissions'
assert not app.findall('service') and not app.findall('receiver'), 'P1 must not add background work'
activity = app.find('activity')
assert activity.get(A + 'screenOrientation') == 'landscape'
assert all(c.get(A + 'name') != 'android.intent.category.HOME'
           for c in activity.findall('.//category')), 'P1 must not take over the device launcher'
build = (ROOT / 'app/build.gradle').read_text()
assert re.search(r'minSdk\s+25\b', build)
assert not re.search(r'^\s*(?:implementation|api|runtimeOnly)\b', build, re.M)
assert not list(main.rglob('*.so')), 'No native ABI dependency expected in P1'
strings = ET.parse(main / 'res/values/strings.xml').getroot()
assert all(not re.search(r'TEST|experimental|preview|prototype|تجريب|معاينة|اختبار', item.text or '', re.I) for item in strings), 'No temporary user-facing copy'
java = '\n'.join(p.read_text() for p in main.rglob('*.java'))
assert not any(name in java for name in ('MediaPlayer', 'LocationManager', 'BluetoothAdapter'))
print('PASS: XML, minSdk25, RTL, landscape, honest production-facing copy, no permissions/services/runtime dependencies/native code')
