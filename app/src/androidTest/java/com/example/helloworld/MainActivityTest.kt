package com.example.helloworld

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun greeting_isDisplayed() {
        // Verify the greeting text is visible on screen
        composeTestRule.onNodeWithText("Hello World!").assertIsDisplayed()
    }

    @Test
    fun greeting_hasCorrectText() {
        // Verify the exact greeting text exists
        composeTestRule
            .onNodeWithText("Hello World!", useUnmergedTree = true)
            .assertExists()
    }
}
