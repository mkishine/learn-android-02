# Project Notes

## User Context

- The user is an experienced Java programmer

## Coding Preferences

### Code Organization
- Define helper functions and classes BELOW the code that uses them, not above

### Testing Style
- Prefer AssertJ assertions over JUnit assertions
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

### Testing Practice
- After fixing a test class, run the test to verify it passes before considering the task complete

### com.example.sandbox Test Classes
- All test classes in this package should have a summary table in the KDoc comment at the top of the class
- When adding new examples, update the summary table to keep it in sync

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
