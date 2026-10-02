#!/usr/bin/env python3
"""P9 non-destructive exact-device baseline collector. Read-only ADB commands only."""
from pathlib import Path
import subprocess, hashlib, json, datetime

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"device-evidence"; OUT.mkdir(exist_ok=True)

def run(args):
    return subprocess.check_output(args,text=True,stderr=subprocess.STDOUT,timeout=60).strip()

devices=[l.split()[0] for l in run(["adb","devices"]).splitlines()[1:] if l.endswith("\tdevice")]
if len(devices)!=1:
    raise SystemExit(f"Need exactly one authorized device; found {len(devices)}")
serial=devices[0]; adb=["adb","-s",serial]

commands={
 "getprop":["shell","getprop"],
 "display":["shell","wm","size"],
 "density":["shell","wm","density"],
 "storage":["shell","df","-h"],
 "packages":["shell","pm","list","packages"],
 "cpuinfo":["shell","cat","/proc/cpuinfo"],
 "meminfo":["shell","cat","/proc/meminfo"],
 "mounts":["shell","cat","/proc/mounts"],
}
raw={}
for name,args in commands.items():
    raw[name]=run(adb+args)
    (OUT/f"{name}.txt").write_text(raw[name]+"\n",encoding="utf-8")

props={}
for line in raw["getprop"].splitlines():
    if "]: [" in line:
        k,v=line.split("]: [",1); props[k.lstrip("[")]=v.rstrip("]")

keys=["ro.build.version.release","ro.build.version.sdk","ro.build.fingerprint","ro.product.board",
      "ro.product.name","ro.product.device","ro.product.model","ro.product.cpu.abi",
      "ro.product.cpu.abilist","ro.hardware","ro.bootloader","ro.build.display.id"]
summary={k:props.get(k,"") for k in keys}
summary["adb_serial"]=serial
summary["display"]=raw["display"]; summary["density"]=raw["density"]
summary["captured_utc"]=datetime.datetime.now(datetime.timezone.utc).isoformat()
(OUT/"baseline.json").write_text(json.dumps(summary,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")

manifest={}
for p in sorted(OUT.glob("*")):
    if p.is_file() and p.name!="SHA256SUMS.txt":
        manifest[p.name]=hashlib.sha256(p.read_bytes()).hexdigest()
(OUT/"SHA256SUMS.txt").write_text("".join(f"{h}  {n}\n" for n,h in manifest.items()),encoding="utf-8")
print(json.dumps(summary,ensure_ascii=False,indent=2))
print(f"PASS: read-only P9 baseline captured in {OUT}")
