package com.example.sandbox

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Kotlin DSL Design Tutorial
 *
 * | Part | Topic                      | Key Concept                                    |
 * |------|----------------------------|------------------------------------------------|
 * | 1    | Basic Lambda with Receiver | `buildString` explained                        |
 * | 2    | Regular vs Receiver Lambda | `(T) -> R` vs `T.() -> R`                      |
 * | 3    | Standard Library Functions | `apply`, `with`, `run`                         |
 * | 4    | Building a Simple DSL      | Nested builders, `person { address { } }`      |
 * | 5    | `@DslMarker`               | Prevents accidental access to outer scopes     |
 * | 6    | Extension + Receiver       | Combining both patterns                        |
 */
class LambdaWithReceiverTest {

    // =========================================================================
    // PART 1: Basic Lambda with Receiver
    // =========================================================================

    @Test
    fun `buildString uses lambda with receiver`() {
        // buildString is a stdlib function with this signature:
        //   inline fun buildString(block: StringBuilder.() -> Unit): String
        //
        // The "StringBuilder.()" means StringBuilder is the RECEIVER.
        // Inside the lambda, 'this' refers to a StringBuilder instance,
        // so you can call append() directly without "sb.append()".

        val name = "Alice"
        val text = buildString {
            // 'this' is StringBuilder here (implicit)
            append("Hello, ")  // same as: this.append("Hello, ")
            append(name)
            append("!")
        }
        assertThat(text).isEqualTo("Hello, Alice!")
    }

    // =========================================================================
    // PART 2: Comparing Regular Lambda vs Lambda with Receiver
    // =========================================================================

    // Regular higher-order function (Java-style approach)
    // The lambda receives StringBuilder as a PARAMETER
    private fun buildStringJavaStyle(block: (StringBuilder) -> Unit): String {
        val sb = StringBuilder()
        block(sb)  // pass sb as an argument to the lambda
        return sb.toString()
    }

    // DSL-style with lambda with receiver
    // The lambda has StringBuilder as its RECEIVER (like 'this')
    private fun buildStringDslStyle(block: StringBuilder.() -> Unit): String {
        val sb = StringBuilder()
        sb.block()  // call the lambda ON sb (sb becomes 'this' inside)
        return sb.toString()
    }

    @Test
    fun `compare Java-style vs DSL-style`() {
        // Java-style: must name the parameter and use it explicitly
        val text1 = buildStringJavaStyle { sb ->
            sb.append("Hello")
            sb.append(" World")
        }

        // DSL-style: cleaner, no parameter needed, methods called directly
        val text2 = buildStringDslStyle {
            append("Hello")
            append(" World")
        }

        assertThat(text1).isEqualTo(text2)
    }

    // =========================================================================
    // PART 3: apply, with, run - Standard Library DSL Functions
    // =========================================================================

    @Test
    fun `apply - configure an object and return it`() {
        // apply signature: inline fun <T> T.apply(block: T.() -> Unit): T
        // - Called ON an object
        // - 'this' inside lambda is that object
        // - Returns the object itself (useful for chaining/configuration)

        data class Person(var name: String = "", var age: Int = 0)

        val person = Person().apply {
            // 'this' is the Person instance
            name = "Bob"      // same as: this.name = "Bob"
            age = 30
        }

        assertThat(person.name).isEqualTo("Bob")
        assertThat(person.age).isEqualTo(30)
    }

    @Test
    fun `with - operate on an object without repeating its name`() {
        // with signature: inline fun <T, R> with(receiver: T, block: T.() -> R): R
        // - Takes receiver as first argument (not called ON it)
        // - Returns whatever the lambda returns

        data class Person(val name: String, val age: Int)
        val person = Person("Alice", 25)

        val description = with(person) {
            // 'this' is person
            "Name: $name, Age: $age"  // last expression is returned
        }

        assertThat(description).isEqualTo("Name: Alice, Age: 25")
    }

    @Test
    fun `run - like 'with' but called on the object`() {
        // run signature: inline fun <T, R> T.run(block: T.() -> R): R
        // - Called ON an object
        // - Returns whatever the lambda returns
        // - Useful when you need null-safety: obj?.run { ... }

        data class Person(val name: String, val age: Int)
        val person = Person("Charlie", 35)

        val description = person.run {
            "Name: $name, Age: $age"
        }

        assertThat(description).isEqualTo("Name: Charlie, Age: 35")
    }

    // =========================================================================
    // PART 4: Building a Simple DSL
    // =========================================================================

    // Step 1: Create builder classes that hold state
    class PersonBuilder {
        var name: String = ""
        var age: Int = 0

        // Nested DSL: address configuration
        private var address: Address? = null

        // This method takes a lambda with receiver!
        fun address(block: AddressBuilder.() -> Unit) {
            val builder = AddressBuilder()
            builder.block()  // execute the lambda with builder as 'this'
            address = builder.build()
        }

        fun build(): PersonWithAddress = PersonWithAddress(name, age, address)
    }

    class AddressBuilder {
        var street: String = ""
        var city: String = ""

        fun build(): Address = Address(street, city)
    }

    data class Address(val street: String, val city: String)
    data class PersonWithAddress(val name: String, val age: Int, val address: Address?)

    // Step 2: Create a top-level DSL entry function
    private fun person(block: PersonBuilder.() -> Unit): PersonWithAddress {
        val builder = PersonBuilder()
        builder.block()
        return builder.build()
    }

    @Test
    fun `custom DSL for building a person`() {
        // Given - a DSL that reads almost like a data structure

        // When
        val alice = person {
            // 'this' is PersonBuilder
            name = "Alice"
            age = 30

            // Nested DSL - 'this' becomes AddressBuilder inside
            address {
                street = "123 Main St"
                city = "Springfield"
            }
        }

        // Then
        assertThat(alice.name).isEqualTo("Alice")
        assertThat(alice.age).isEqualTo(30)
        assertThat(alice.address?.street).isEqualTo("123 Main St")
        assertThat(alice.address?.city).isEqualTo("Springfield")
    }

    // =========================================================================
    // PART 5: @DslMarker - Preventing Scope Leakage
    // =========================================================================

    // Problem: In nested lambdas, you can accidentally access outer receivers.
    // @DslMarker creates a "scope boundary" - you must be explicit to access outer scope.

    @DslMarker
    annotation class HtmlDsl  // Custom marker annotation

    @HtmlDsl
    class HtmlBuilder {
        private val content = StringBuilder()

        fun body(block: BodyBuilder.() -> Unit) {
            content.append("<body>")
            val bodyBuilder = BodyBuilder()
            bodyBuilder.block()
            content.append(bodyBuilder.build())
            content.append("</body>")
        }

        fun build(): String = content.toString()
    }

    @HtmlDsl
    class BodyBuilder {
        private val content = StringBuilder()

        fun p(text: String) {
            content.append("<p>$text</p>")
        }

        fun div(block: BodyBuilder.() -> Unit) {
            content.append("<div>")
            val divBuilder = BodyBuilder()
            divBuilder.block()
            content.append(divBuilder.build())
            content.append("</div>")
        }

        // Without @DslMarker, inside div { } you could accidentally call
        // body { } from the outer HtmlBuilder scope. The marker prevents this.

        fun build(): String = content.toString()
    }

    private fun html(block: HtmlBuilder.() -> Unit): String {
        val builder = HtmlBuilder()
        builder.block()
        return builder.build()
    }

    @Test
    fun `HTML DSL with DslMarker`() {
        val result = html {
            body {
                p("Hello")
                div {
                    p("Nested paragraph")
                    // body { } // <-- This would NOT compile thanks to @DslMarker!
                    //            // It prevents accidentally calling outer scope methods.
                    //            // You'd have to write: this@html.body { } to be explicit.
                }
            }
        }

        assertThat(result).isEqualTo("<body><p>Hello</p><div><p>Nested paragraph</p></div></body>")
    }

    // =========================================================================
    // PART 6: Extension Functions + Lambda with Receiver = Powerful Combo
    // =========================================================================

    // Extension function that uses lambda with receiver
    private fun <T> T.configure(block: T.() -> Unit): T {
        this.block()
        return this
    }

    @Test
    fun `extension function with lambda receiver`() {
        data class Config(var debug: Boolean = false, var maxRetries: Int = 3)

        val config = Config().configure {
            debug = true
            maxRetries = 5
        }

        assertThat(config.debug).isTrue()
        assertThat(config.maxRetries).isEqualTo(5)
    }
}