#!/usr/bin/env python3
"""Run the same pure-Java focused checks used by androidTest, without new dependencies."""
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
sources = sorted((ROOT / 'app/src/main/java/com/abosultan/darbakos/core').glob('*.java'))
sources.append(ROOT / 'app/src/androidTest/java/com/abosultan/darbakos/MonitoringFocusedChecks.java')
with tempfile.TemporaryDirectory(prefix='darbak-monitoring-') as classes:
    subprocess.run(['java', '-m', 'jdk.compiler/com.sun.tools.javac.Main', '-d', classes,
                    *map(str, sources)], check=True)
    subprocess.run(['java', '-cp', classes, 'com.abosultan.darbakos.MonitoringFocusedChecks'], check=True)
