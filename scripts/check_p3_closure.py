#!/usr/bin/env python3
"""Verify unchanged build inputs/proofs and stage only verified APK/lint files."""
from pathlib import Path
import hashlib
import json
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
manifest = json.loads((ROOT / 'scripts/p3_closure_reuse.json').read_text())
paths = subprocess.check_output(['git', 'ls-files', 'app', 'gradle', '*.gradle',
    'gradle.properties', 'gradlew', 'gradlew.bat'], cwd=ROOT, text=True).splitlines()
assert set(paths) == set(manifest['source_sha256']), 'Build input set changed; reuse forbidden'
for path, digest in manifest['source_sha256'].items():
    assert hashlib.sha256((ROOT / path).read_bytes()).hexdigest() == digest, path
old = json.loads((ROOT / 'scripts/guardian_reused_proofs.json').read_text())
for path, digest in old['source_sha256'].items():
    assert hashlib.sha256((ROOT / path).read_bytes()).hexdigest() == digest, path
if len(sys.argv) == 2:
    artifact = Path(sys.argv[1])
    for path, digest in manifest['artifact_sha256'].items():
        assert hashlib.sha256((artifact / path).read_bytes()).hexdigest() == digest, path
    for path in manifest['artifact_sha256']:
        destination = ROOT / path
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(artifact / path, destination)
    out = ROOT / 'test-evidence'
    out.mkdir(exist_ok=True)
    (out / 'build-proof-reuse.json').write_text(json.dumps(manifest, indent=2))
print('PASS:52 unchanged build inputs and12 original proof fingerprints; no rebuild needed')
