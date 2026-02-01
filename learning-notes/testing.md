# Automated Testing Approaches for Android Apps

## 1. Unit Tests (Local JVM)

**Location:** `app/src/test/java/com/example/helloworld/`

Fast tests that run on your development machine without an emulator:

- Test pure Kotlin logic, ViewModels, data transformations
- Uses JUnit 4 (`junit:junit:4.13.2`)
- No Android framework dependencies (or use mocks)

Run with:

```bash
JAVA_HOME=/opt/android-studio/jbr ./gradlew test
```

Example:

```kotlin
class GreetingTest {
    @Test
    fun greeting_returnsExpectedText() {
        assertEquals("Hello World!", getGreetingText())
    }
}
```

## 2. Compose UI Tests (Instrumented)

**Location:** `app/src/androidTest/java/com/example/helloworld/`

Tests that verify Compose UI behavior on an emulator or device:

- Uses `ui-test-junit4` library
- Can test composables in isolation or full activities
- Semantic-based matching (find by text, content description, test tags)

Run with:

```bash
JAVA_HOME=/opt/android-studio/jbr ANDROID_HOME=~/Android/Sdk ./gradlew connectedAndroidTest
```

Example (activity test):

```kotlin
@get:Rule
val composeTestRule = createAndroidComposeRule<MainActivity>()

@Test
fun greeting_isDisplayed() {
    composeTestRule.onNodeWithText("Hello World!").assertIsDisplayed()
}
```

Example (composable in isolation):

```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun greeting_displaysNameCorrectly() {
    composeTestRule.setContent {
        Greeting(name = "Android")
    }
    composeTestRule.onNodeWithText("Hello Android!").assertIsDisplayed()
}
```

## 3. Espresso Tests

**Location:** `app/src/androidTest/java/com/example/helloworld/`

Traditional Android UI testing framework:

- Uses `espresso-core` library
- Best for View-based (non-Compose) UI
- Can also test system interactions

Example:

```kotlin
@Test
fun buttonClick_showsMessage() {
    onView(withId(R.id.myButton)).perform(click())
    onView(withText("Clicked!")).check(matches(isDisplayed()))
}
```

## 4. Screenshot Tests

Catch visual regressions by comparing screenshots:

**Paparazzi** (runs on JVM, no device needed):

```kotlin
// build.gradle.kts
id("app.cash.paparazzi") version "1.3.1"
```

```kotlin
@get:Rule
val paparazzi = Paparazzi()

@Test
fun greeting_screenshot() {
    paparazzi.snapshot {
        Greeting("World")
    }
}
```

Run with:

```bash
./gradlew recordPaparazziDebug  # Record baseline
./gradlew verifyPaparazziDebug  # Compare against baseline
```

## 5. End-to-End Tests with UI Automator

Test across app boundaries (system UI, notifications, other apps):

Add dependency:

```kotlin
androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
```

Example:

```kotlin
@Test
fun openAppFromHomeScreen() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Press home
    device.pressHome()

    // Open app drawer and launch app
    val appIcon = device.findObject(UiSelector().text("HelloWorld"))
    appIcon.clickAndWaitForNewWindow()

    // Verify app launched
    val greeting = device.findObject(UiSelector().text("Hello World!"))
    assertTrue(greeting.exists())
}
```

## Comparison

| Approach         | Speed  | Requires Device | Best For                   |
|------------------|--------|-----------------|----------------------------|
| Unit Tests       | Fast   | No              | Business logic, ViewModels |
| Compose UI Tests | Medium | Yes             | Compose UI behavior        |
| Espresso Tests   | Medium | Yes             | View-based UI              |
| Screenshot Tests | Fast   | No (Paparazzi)  | Visual regression          |
| UI Automator     | Slow   | Yes             | Cross-app, system UI       |

## Recommended Starting Point

For a Compose app:

1. Start with **Compose UI tests** for UI behavior
2. Add **unit tests** as you add business logic
3. Consider **screenshot tests** if visual consistency matters
