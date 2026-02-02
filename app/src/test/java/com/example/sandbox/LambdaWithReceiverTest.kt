package com.example.sandbox

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LambdaWithReceiverTest {
    @Test
    fun test() {
        val name = "Alice"
        val text = buildString {
            append("Hello, ")
            append(name)
            append("!")
        }
        assertThat(text).isEqualTo("Hello, Alice!")
    }
}