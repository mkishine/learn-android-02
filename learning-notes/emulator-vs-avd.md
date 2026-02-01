# Emulator Binary vs AVD

## Overview

| Term                              | What it is                                                                         |
|-----------------------------------|------------------------------------------------------------------------------------|
| `~/Android/Sdk/emulator/emulator` | The **emulator program** (executable binary) that runs virtual devices             |
| `Small_Phone`                     | An **AVD (Android Virtual Device)** — a configuration that defines a virtual phone |

## Analogy

Think of it like a DVD player and a DVD:
- **Emulator binary** = the DVD player (the software that plays things)
- **AVD (`Small_Phone`)** = a specific DVD (the content/configuration to play)

## What an AVD Contains

An AVD like `Small_Phone` defines:
- Screen size and resolution
- Android API level (e.g., Android 35)
- System image (x86_64, ARM)
- Hardware profile (RAM, storage, sensors)
- Device skin/appearance

## Commands

List available AVDs:
```bash
~/Android/Sdk/emulator/emulator -list-avds
```

Launch a specific AVD:
```bash
~/Android/Sdk/emulator/emulator -avd Small_Phone
```

This tells the emulator program (`emulator`) to launch the virtual device configuration named `Small_Phone`.

You can have multiple AVDs (e.g., `Small_Phone`, `Medium_Phone`, `Tablet`) and use the same emulator binary to run any of them.

## Check if an AVD is Running

Use `adb devices` to see connected emulators and devices:
```bash
~/Android/Sdk/platform-tools/adb devices
```

Example output when an emulator is running:
```
List of devices attached
emulator-5554	device
```

| Status         | Meaning                                         |
|----------------|-------------------------------------------------|
| `device`       | Running and ready to use                        |
| `offline`      | Not responding (may be booting)                 |
| `unauthorized` | USB debugging not authorized on physical device |

If multiple emulators are running, they appear as `emulator-5554`, `emulator-5556`, etc.
