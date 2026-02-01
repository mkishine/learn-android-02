package com.example.helloworld

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the greetingText function.
 * These run on the local JVM without an Android device.
 */
class GreetingTextTest {

    @Test
    fun greetingText_withName_returnsCorrectGreeting() {
        val result = greetingText("World")
        assertEquals("Hello World!", result)
    }

    @Test
    fun greetingText_withDifferentName_returnsCorrectGreeting() {
        val result = greetingText("Android")
        assertEquals("Hello Android!", result)
    }

    @Test
    fun greetingText_withEmptyName_returnsGreetingWithEmptyName() {
        val result = greetingText("")
        assertEquals("Hello !", result)
    }

    @Test
    fun greetingText_withSpecialCharacters_handlesCorrectly() {
        val result = greetingText("User123")
        assertEquals("Hello User123!", result)
    }

    @Test
    fun greetingText_withSpaces_handlesCorrectly() {
        val result = greetingText("John Doe")
        assertEquals("Hello John Doe!", result)
    }
}
