# Project Notes

## Coding Preferences

### Testing Style
- Use Given/When/Then comments in test methods when they contain more than a few lines
- Example:
  ```kotlin
  @Test
  fun complexTest() {
      // Given
      val input = setupTestData()

      // When
      val result = performAction(input)

      // Then
      assertEquals(expected, result)
  }
  ```

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
