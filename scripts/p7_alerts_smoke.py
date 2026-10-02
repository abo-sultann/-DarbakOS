#!/usr/bin/env python3
from pathlib import Path
import subprocess
ROOT=Path(__file__).resolve().parents[1]; OUT=ROOT/'test-evidence'; OUT.mkdir(exist_ok=True)
serials=[x.split()[0] for x in subprocess.check_output(['adb','devices'],text=True).splitlines()[1:] if x.endswith('\tdevice') and x.startswith('emulator-')]
assert len(serials)==1
ADB=['adb','-s',serials[0]]
assert subprocess.check_output(ADB+['shell','getprop','ro.build.version.sdk'],text=True).strip()=='25'
sel='com.abosultan.darbakos.ShellTest#actionableAlertIsQuietWhenGpsPermissionExistsAndVisibleWhenMissing'
out=subprocess.check_output(ADB+['shell','am','instrument','-w','-e','class',sel,'com.abosultan.darbakos.test/androidx.test.runner.AndroidJUnitRunner'],text=True,timeout=180)
(OUT/'p7-alerts-instrumentation.txt').write_text(out)
assert 'OK (1 test)' in out and 'FAILURES' not in out,out
print('PASS: P7 actionable alerts quiet/actionable states; 1 focused test')
