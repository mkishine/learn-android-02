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
```text
~/Android/Sdk/platform-tools/adb shell am start -n com.example.helloworld/.MainActivity
```

## Stop
```text
~/Android/Sdk/platform-tools/adb shell am force-stop com.example.helloworld
```

## Uninstall
```text
~/Android/Sdk/platform-tools/adb uninstall com.example.helloworld
```
