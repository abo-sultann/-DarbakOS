#!/usr/bin/env python3
from pathlib import Path
import subprocess
ROOT=Path(__file__).resolve().parents[1]; OUT=ROOT/'test-evidence'; OUT.mkdir(exist_ok=True)
serials=[x.split()[0] for x in subprocess.check_output(['adb','devices'],text=True).splitlines()[1:] if x.endswith('\tdevice') and x.startswith('emulator-')]
assert len(serials)==1
ADB=['adb','-s',serials[0]]
assert subprocess.check_output(ADB+['shell','getprop','ro.build.version.sdk'],text=True).strip()=='25'
pkg='com.abosultan.darbakos.test'
runner=pkg+'/androidx.test.runner.AndroidJUnitRunner'
def run(sel):
    out=subprocess.check_output(ADB+['shell','am','instrument','-w','-e','class',sel,runner],text=True,timeout=180)
    assert 'OK (1 test)' in out and 'FAILURES' not in out,out
    return out
subprocess.run(ADB+['shell','pm','grant',pkg,'android.permission.ACCESS_FINE_LOCATION'],check=True)
quiet=run('com.abosultan.darbakos.ShellTest#actionableAlertIsQuietWhenGpsPermissionExists')
subprocess.run(ADB+['shell','pm','revoke',pkg,'android.permission.ACCESS_FINE_LOCATION'],check=True)
missing=run('com.abosultan.darbakos.ShellTest#actionableAlertShowsWhenGpsPermissionMissing')
subprocess.run(ADB+['shell','pm','grant',pkg,'android.permission.ACCESS_FINE_LOCATION'],check=True)
(OUT/'p7-alerts-instrumentation.txt').write_text(quiet+'\\n--- missing permission ---\\n'+missing)
print('PASS: P7 actionable alerts quiet/actionable states; 2 focused tests')
