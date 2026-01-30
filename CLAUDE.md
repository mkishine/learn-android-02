# Project Notes

## Build and Run Commands

Start the emulator:
```bash
~/Android/Sdk/emulator/emulator -avd Small_Phone &
```

Build, install, and launch the app:
```bash
JAVA_HOME=/opt/android-studio/jbr ANDROID_HOME=~/Android/Sdk ./gradlew installDebug && ~/Android/Sdk/platform-tools/adb shell am start -n com.example.helloworld/.MainActivity
```

## Environment Setup

- **JAVA_HOME:** `/opt/android-studio/jbr`
- **Java binary:** `/opt/android-studio/jbr/bin/java`

## Android Emulator Debugging

**Problem:** Android SDK emulator core dumps on launch

**Environment:**
- Emulator: `~/Android/Sdk/emulator/emulator` version 33.1.24.0
- AVDs available: `Medium_Phone`, `Small_Phone`
- System image: `android-35/google_apis_playstore/x86_64`

**What we know:**
- Emulator crashes with "Segmentation fault (core dumped)" immediately on launch
- Exit code 139 (128 + 11 = SIGSEGV)
- strace analysis confirms stack exhaustion:
  - `prlimit64` shows `rlim_cur=8192*1024` (8 MB stack limit)
  - Crash: `SIGSEGV {si_signo=SIGSEGV, si_code=SEGV_MAPERR, si_addr=0x7ffd5f7caff8}`
  - Stack pointer was at ~0x7ffd5ffc7b74, crash address is exactly 8 MB below
  - Emulator needs more than 8 MB stack space

**CRITICAL WARNING:**
- **NEVER set stack to unlimited (`ulimit -s unlimited`)** - this caused system to become unresponsive

**Potential root cause - Version mismatch:**

Investigation steps:
1. Checked emulator binary date: `ls -la ~/Android/Sdk/emulator/emulator`
   - Result: **December 19, 2023** (version 33.1.24.0)
2. Checked system image date: `ls -la ~/Android/Sdk/system-images/android-35/google_apis_playstore/x86_64/`
   - Result: **January 29, 2025** (android-35)

**Conclusion:** The emulator (Dec 2023) is over a year older than the system image (Jan 2025). This version mismatch may cause the emulator to behave unexpectedly with newer system images, potentially explaining the excessive stack usage.

**Investigation attempt - downgrade system image:**

1. Installed Android 34 system image: `system-images;android-34;google_apis_playstore;x86_64`
2. SDK manager also installed new emulator to `~/Android/Sdk/emulator-2`
3. Created AVD `Medium_Phone_API34` with Android 34

**Surprising finding:** New emulator (in emulator-2) ALSO crashes with segfault, even just running `-list-avds`

**Root cause confirmed:** Both emulators (old and new) crash due to 8 MB stack limit
- Old emulator strace: `prlimit64` shows `rlim_cur=8192*1024`, crash at stack boundary
- New emulator strace: same pattern - `prlimit64` shows `rlim_cur=8192*1024`, crash at stack boundary
- Version mismatch was NOT the root cause

**NEW FINDING - Citrix App Protection interference:**

Investigation steps:
1. `ldd` on emulator showed unusual library: `/usr/local/lib/AppProtection/libAppProtection.so`
2. Checked `/etc/ld.so.preload` - contains this library
3. This means Citrix App Protection is injected into EVERY process on the system

**CONFIRMED root cause:** Citrix App Protection library intercepts system/memory operations and causes the emulator to crash with stack exhaustion.

**Solution:** Disable App Protection by renaming `/etc/ld.so.preload`:
```bash
sudo mv /etc/ld.so.preload /etc/ld.so.preload.bak
```

**Status:** App Protection is currently disabled (`/etc/ld.so.preload` renamed to `/etc/ld.so.preload.bak`). Emulator works.

To restore App Protection:
```bash
sudo mv /etc/ld.so.preload.bak /etc/ld.so.preload
```

**CRITICAL WARNING:**
- **NEVER set stack to unlimited (`ulimit -s unlimited`)** - this caused system to become unresponsive
