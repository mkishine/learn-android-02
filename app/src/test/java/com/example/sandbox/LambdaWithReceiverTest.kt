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

    // =========================================================================
    // PART 3: apply, with, run - Standard Library DSL Functions
    // =========================================================================

    @Test
    fun `apply - configure an object and return it`() {
        // apply signature: inline fun <T> T.apply(block: T.() -> Unit): T
        // - Called ON an object
        // - 'this' inside lambda is that object
        // - RETURN VALUE: Always returns the receiver object itself (ignores lambda result)
        //   This makes apply ideal for object configuration and builder patterns.

        data class Person(var name: String = "", var age: Int = 0)

        val original = Person()
        val person = original.apply {
            // 'this' is the Person instance
            name = "Bob"      // same as: this.name = "Bob"
            age = 30
            "this string is ignored"  // lambda's last expression is discarded
        }

        assertThat(person.name).isEqualTo("Bob")
        assertThat(person.age).isEqualTo(30)
        // apply returns the same object instance, not the lambda result
        assertThat(person).isSameAs(original)
    }

    @Test
    fun `with - operate on an object without repeating its name`() {
        // with signature: inline fun <T, R> with(receiver: T, block: T.() -> R): R
        // - Takes receiver as first argument (not called ON it)
        // - RETURN VALUE: Returns the lambda's last expression (type R)
        //   Unlike apply, the receiver is NOT returned - you get the lambda result.

        // Given
        data class Person(val name: String, val age: Int)
        val person = Person("Alice", 25)

        // When
        val description = with(person) {
            // 'this' is person
            "Name: $name, Age: $age"  // last expression is returned
        }

        // Then
        assertThat(description).isEqualTo("Name: Alice, Age: 25")
        // with returns the lambda result (String), not the receiver (Person)
        assertThat(description).isInstanceOf(String::class.java)
    }

    @Test
    fun `with - returns computed value from lambda`() {
        // Given
        data class Person(val name: String, val age: Int)
        val person = Person("Alice", 25)

        // When - returning a computed Boolean value
        val isAdult = with(person) {
            age >= 18  // returns Boolean, not Person
        }

        // Then
        assertThat(isAdult).isTrue()
    }

    @Test
    fun `run - like 'with' but called on the object`() {
        // run signature: inline fun <T, R> T.run(block: T.() -> R): R
        // - Called ON an object
        // - RETURN VALUE: Returns the lambda's last expression (type R), just like 'with'
        //   The difference from apply: run returns the lambda result, apply returns the receiver.
        // - Useful when you need null-safety: obj?.run { ... }

        data class Person(val name: String, val age: Int)
        val person = Person("Charlie", 35)

        val description = person.run {
            "Name: $name, Age: $age"
        }

        assertThat(description).isEqualTo("Name: Charlie, Age: 35")
        // run returns the lambda result (String), not the receiver (Person)
        assertThat(description).isInstanceOf(String::class.java)

        // Contrast with apply: if we used apply here, we'd get Person back
        val fromApply = person.apply { "Name: $name" }
        assertThat(fromApply).isSameAs(person)  // apply returns the receiver

        val fromRun = person.run { "Name: $name" }
        assertThat(fromRun).isEqualTo("Name: Charlie")  // run returns the lambda result
    }

    // =========================================================================
    // PART 4: Building a Simple DSL
    // =========================================================================

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

    // DSL entry function
    private fun person(block: PersonBuilder.() -> Unit): PersonWithAddress {
        val builder = PersonBuilder()
        builder.block()
        return builder.build()
    }

    // Builder classes that hold state
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

    // =========================================================================
    // PART 5: @DslMarker - Preventing Scope Leakage
    // =========================================================================

    // Problem: In nested lambdas, you can accidentally access outer receivers.
    // @DslMarker creates a "scope boundary" - you must be explicit to access outer scope.

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

    private fun html(block: HtmlBuilder.() -> Unit): String {
        val builder = HtmlBuilder()
        builder.block()
        return builder.build()
    }

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

    // =========================================================================
    // PART 6: Extension Functions + Lambda with Receiver = Powerful Combo
    // =========================================================================

    @Test
    fun `extension function with lambda receiver`() {
        data class Config(var debug: Boolean = false, var maxRetries: Int = 3)
        data class AnotherConfig(var timeout: Int = 1000)

        val config = Config().configure {
            debug = true
            maxRetries = 5
        }
        val anotherConfig = AnotherConfig().configure {
            timeout = 200
        }

        assertThat(config.debug).isTrue()
        assertThat(config.maxRetries).isEqualTo(5)
        assertThat(anotherConfig.timeout).isEqualTo(5)
    }

    // Extension function that uses lambda with receiver
    private fun <T> T.configure(block: T.() -> Unit): T {
        this.block()
        return this
    }
}