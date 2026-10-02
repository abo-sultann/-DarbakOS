#!/usr/bin/env python3
"""Focused API25 verification for P8 recovery readiness."""
from pathlib import Path
import subprocess
ROOT=Path(__file__).resolve().parents[1]; OUT=ROOT/'test-evidence'; OUT.mkdir(exist_ok=True)
PACKAGE='com.abosultan.darbakos.test'
serials=[l.split()[0] for l in subprocess.check_output(['adb','devices'],text=True).splitlines()[1:] if l.endswith('\tdevice') and l.startswith('emulator-')]
assert len(serials)==1
ADB=['adb','-s',serials[0]]
assert subprocess.check_output(ADB+['shell','getprop','ro.build.version.sdk'],text=True).strip()=='25'
selected='com.abosultan.darbakos.RecoveryReadinessTest'
out=subprocess.check_output(ADB+['shell','am','instrument','-w','-e','class',selected,PACKAGE+'.test/androidx.test.runner.AndroidJUnitRunner'],text=True,timeout=180)
(OUT/'p8-recovery-readiness.txt').write_text(out)
assert 'OK (1 test)' in out and 'FAILURES' not in out,out
print('PASS: P8 recovery readiness contract; 1 focused test')
