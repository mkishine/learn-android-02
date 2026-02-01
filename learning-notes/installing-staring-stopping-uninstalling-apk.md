# Brief
Today I have figured out how to install apk file, run activity, stop and then uninstall the package.

# Notes
## Install
First:
```text
~/Android/Sdk/emulator/emulator -avd Small_Phone < /dev/null > ./build/emulator.out 2>&1 &
```
Then:
```text
~/Android/Sdk/platform-tools/adb install -r ./app/build/outputs/apk/debug/app-debug.apk
```

## Run

### Option 1: Explicit (by component name)
```text
~/Android/Sdk/platform-tools/adb shell am start -n com.example.helloworld/.MainActivity
```

### Option 2: Intent-based (by intent filter)
```text
~/Android/Sdk/platform-tools/adb shell am start -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -p com.example.helloworld
```

| Flag | Meaning |
|------|---------|
| `-a android.intent.action.MAIN` | Specifies the **action** — `MAIN` means "entry point, don't expect data" |
| `-c android.intent.category.LAUNCHER` | Specifies the **category** — `LAUNCHER` means "should appear in app launcher" |
| `-p com.example.helloworld` | Restricts to this **package** only |

Android's Activity Manager searches for an activity whose `<intent-filter>` matches all three criteria. This mimics how the Android home screen launcher starts apps. If you later rename `MainActivity`, this command still works as long as the intent-filter remains.

## Stop
```text
~/Android/Sdk/platform-tools/adb shell am force-stop com.example.helloworld
```

## Uninstall
```text
~/Android/Sdk/platform-tools/adb uninstall com.example.helloworld
```
