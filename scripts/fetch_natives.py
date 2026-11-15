#!/usr/bin/env python3
"""Pins third-party Android .so files by Git object SHA and basic ELF checks.

Source binary redistribution is subject to the original project's license and
transitive library licenses; review these before public distribution.
"""
from pathlib import Path
import hashlib
import struct
import subprocess
from urllib.request import urlopen

COMMIT = 'aefac0a381441132772f081d806cda9476628394'
REPO = 'https://raw.githubusercontent.com/melon4ik666/voicechat-android-fix/'
NATIVES = {
    'liblame4j.so': '6cfeb6c9f72212ad0b4442362a8d0a548b78399f',
    'libopus4j.so': '138b952cc19b734dadfbe24b8ac21f95910855d1',
    'librnnoise4j.so': 'b32b1d944bad1aa45bdc29c849a2361b1fb004aa',
    
'libspeex4j.so': 'f4cf9f4ab5cca6c05b2b02640ff7305accff2e5d',

}
TARGET = Path('src/main/resources/natives/android-aarch64')
TARGET.mkdir(parents=True, exist_ok=True)

for name, sha in NATIVES.items():
    url = REPO + COMMIT + '/src/main/resources/natives/android-aarch64/' + name
    print('Fetching', name)
    with urlopen(url, timeout=60) as response:
        data = response.read()
    git_sha = hashlib.sha1(f'blob {len(data)}\0'.encode() + data).hexdigest()
    if git_sha != sha:
        raise RuntimeError(f'Git SHA mismatch for {name}: {git_sha} != {sha}')
    if data[:4] != b'\x7fELF' or data[4] != 2 or data[5] != 1:
        raise RuntimeError(f'Not an ELF64 little-endian library: {name}')
    if struct.unpack_from('<H', data, 18)[0] != 183:
        raise RuntimeError(f'Not AArch64: {name}')
    path = TARGET / name
    path.write_bytes(data)
    dynamic = subprocess.run(['readelf', '-d', str(path)], text=True, capture_output=True, check=True).stdout
    for forbidden in ('libm.so.6', 'libpthread.so.0', 'ld-linux-aarch64.so.1', 'libc.so.6'):
        if forbidden in dynamic:
            raise RuntimeError(f'Unexpected GLIBC dependency in {name}: {forbidden}')
    print('OK:', name, len(data), 'bytes, blob', git_sha)
print('All Android libraries staged.')
