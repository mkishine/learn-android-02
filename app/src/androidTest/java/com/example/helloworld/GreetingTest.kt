package com.example.helloworld

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.helloworld.ui.theme.HelloWorldTheme
import org.junit.Rule
import org.junit.Test

/**
 * Tests the Greeting composable in isolation without launching MainActivity.
 * This approach is faster and more focused than full activity tests.
 */
class GreetingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun greeting_displaysNameCorrectly() {
        composeTestRule.setContent {
            HelloWorldTheme {
                Greeting(name = "Android")
            }
        }

        composeTestRule.onNodeWithText("Hello Android!").assertIsDisplayed()
    }

    @Test
    fun greeting_withDifferentName_displaysCorrectly() {
        composeTestRule.setContent {
            HelloWorldTheme {
                Greeting(name = "Test")
            }
        }

        composeTestRule.onNodeWithText("Hello Test!").assertIsDisplayed()
    }

    @Test
    fun greeting_withEmptyName_displaysCorrectly() {
        composeTestRule.setContent {
            HelloWorldTheme {
                Greeting(name = "")
            }
        }

        composeTestRule.onNodeWithText("Hello !").assertIsDisplayed()
    }
}
