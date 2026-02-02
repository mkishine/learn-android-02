package com.example.helloworld

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Unit tests for the greetingText function.
 * These run on the local JVM without an Android device.
 */
class GreetingTextTest {

    @Test
    fun greetingText_withName_returnsCorrectGreeting() {
        val result = greetingText("World")
        assertThat(result).isEqualTo("Hello World!")
    }

    @Test
    fun greetingText_withDifferentName_returnsCorrectGreeting() {
        val result = greetingText("Android")
        assertThat(result).isEqualTo("Hello Android!")
    }

    @Test
    fun greetingText_withEmptyName_returnsGreetingWithEmptyName() {
        val result = greetingText("")
        assertThat(result).isEqualTo("Hello !")
    }

    @Test
    fun greetingText_withSpecialCharacters_handlesCorrectly() {
        val result = greetingText("User123")
        assertThat(result).isEqualTo("Hello User123!")
    }

    @Test
    fun greetingText_withSpaces_handlesCorrectly() {
        val result = greetingText("John Doe")
        assertThat(result).isEqualTo("Hello John Doe!")
    }
}
