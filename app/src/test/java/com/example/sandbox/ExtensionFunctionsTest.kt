package com.example.sandbox

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Demonstrates Kotlin's extension functions and extension properties.
 *
 * Extension functions allow adding new functions to existing classes without modifying them
 * or using inheritance. They are resolved statically at compile time based on the declared
 * type, not the runtime type.
 *
 * ## Summary Table
 *
 * | Test Method                              | Concept Demonstrated                                    |
 * |------------------------------------------|---------------------------------------------------------|
 * | `basicExtensionFunction`                 | Adding a function to an existing class                  |
 * | `extensionFunctionWithParameters`        | Extension functions can take parameters                 |
 * | `extensionFunctionOnNullableType`        | Extensions on nullable types handle null safely         |
 * | `extensionProperty`                      | Adding computed properties to existing classes          |
 * | `extensionFunctionWithGenerics`          | Generic extension functions                             |
 * | `memberFunctionTakesPrecedence`          | Member functions win over extensions with same name     |
 * | `extensionsAreResolvedStatically`        | Extensions use declared type, not runtime type          |
 * | `extensionOnStandardLibraryTypes`        | Extending String, List, and other standard types        |
 * | `extensionFunctionScope`                 | Extensions can access public members of receiver        |
 * | `chainedExtensionFunctions`              | Building fluent APIs with extension functions           |
 */
class ExtensionFunctionsTest {

    // region Basic Extension Functions

    @Test
    fun basicExtensionFunction() {
        // Extension functions are declared outside the class they extend.
        // The type being extended is called the "receiver type".
        // Inside the function, `this` refers to the receiver object.

        // Given
        val greeting = "hello"

        // When - calling our extension function on String
        val result = greeting.addExclamation()

        // Then
        assertThat(result).isEqualTo("hello!")
    }

    @Test
    fun extensionFunctionWithParameters() {
        // Given
        val base = "Hello"

        // When - extension function with a parameter
        val result = base.repeat(3, separator = ", ")

        // Then
        assertThat(result).isEqualTo("Hello, Hello, Hello")
    }

    // endregion

    // region Extensions on Nullable Types

    @Test
    fun extensionFunctionOnNullableType() {
        // Extensions can be defined on nullable types (String?).
        // Inside such extension, `this` can be null, so you must handle it.

        // Given
        val nullString: String? = null
        val nonNullString: String? = "hello"

        // When - calling extension defined on String?
        val nullResult = nullString.toSafeUpperCase()
        val nonNullResult = nonNullString.toSafeUpperCase()

        // Then
        assertThat(nullResult).isEqualTo("N/A")
        assertThat(nonNullResult).isEqualTo("HELLO")
    }

    // endregion

    // region Extension Properties

    @Test
    fun extensionProperty() {
        // Extension properties add computed properties to existing classes.
        // They cannot have backing fields (no state storage).
        // They must be computed from the receiver's existing state.

        // Given
        val text = "Kotlin"

        // When - accessing extension properties
        val lastChar = text.lastChar
        val isPalindrome = "radar".isPalindrome

        // Then
        assertThat(lastChar).isEqualTo('n')
        assertThat(isPalindrome).isTrue()
        assertThat("hello".isPalindrome).isFalse()
    }

    // endregion

    // region Generic Extension Functions

    @Test
    fun extensionFunctionWithGenerics() {
        // Extension functions can be generic, working with any type.

        // Given
        val numbers = listOf(1, 2, 3, 4, 5)
        val strings = listOf("a", "b", "c")

        // When - using generic extension
        val secondNumber = numbers.secondOrNull()
        val secondString = strings.secondOrNull()
        val emptyResult = emptyList<Int>().secondOrNull()

        // Then
        assertThat(secondNumber).isEqualTo(2)
        assertThat(secondString).isEqualTo("b")
        assertThat(emptyResult).isNull()
    }

    // endregion

    // region Member vs Extension Precedence

    @Test
    fun memberFunctionTakesPrecedence() {
        // If a class has a member function with the same signature as an extension,
        // the member function always wins.

        // Given
        val sample = SampleClass("test")

        // When - calling a method that exists both as member and extension
        val result = sample.describe()

        // Then - member function is called, not the extension
        assertThat(result).isEqualTo("Member: test")
    }

    // endregion

    // region Static Resolution

    @Test
    fun extensionsAreResolvedStatically() {
        // Extensions are resolved at compile time based on the DECLARED type,
        // not the runtime type. This is different from virtual method dispatch.

        // Given
        val parent: Parent = Child()  // Declared as Parent, runtime type is Child

        // When - calling extension function
        val result = parent.getType()

        // Then - Parent's extension is called because declared type is Parent
        assertThat(result).isEqualTo("Parent")

        // Contrast: if we declare as Child
        val child: Child = Child()
        assertThat(child.getType()).isEqualTo("Child")
    }

    // endregion

    // region Extensions on Standard Library Types

    @Test
    fun extensionOnStandardLibraryTypes() {
        // You can extend any class, including standard library types.

        // Given
        val numbers = listOf(1, 2, 3, 4, 5)

        // When - using extensions on standard types
        val sumOfSquares = numbers.sumOfSquares()
        val words = "hello world kotlin".words()

        // Then
        assertThat(sumOfSquares).isEqualTo(55)  // 1 + 4 + 9 + 16 + 25
        assertThat(words).containsExactly("hello", "world", "kotlin")
    }

    // endregion

    // region Extension Function Scope

    @Test
    fun extensionFunctionScope() {
        // Inside an extension function:
        // - `this` refers to the receiver object
        // - Can access public members of the receiver
        // - Cannot access private or protected members

        // Given
        val person = Person("Alice", 30)

        // When - extension accesses public properties
        val info = person.info()

        // Then
        assertThat(info).isEqualTo("Alice is 30 years old")
    }

    // endregion

    // region Fluent APIs with Extensions

    @Test
    fun chainedExtensionFunctions() {
        // Extension functions enable building fluent, chainable APIs.

        // Given
        val text = "  hello world  "

        // When - chaining extension functions
        val result = text
            .trimmed()
            .capitalized()
            .wrapped("<<", ">>")

        // Then
        assertThat(result).isEqualTo("<<Hello World>>")
    }

    // endregion

    // region Extension Function Definitions (defined below the code that uses them)

    private fun String.addExclamation(): String = this + "!"

    private fun String.repeat(times: Int, separator: String): String =
        (1..times).joinToString(separator) { this }

    private fun String?.toSafeUpperCase(): String = this?.uppercase() ?: "N/A"

    private val String.lastChar: Char
        get() = this[length - 1]

    private val String.isPalindrome: Boolean
        get() = this == this.reversed()

    private fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null

    private fun List<Int>.sumOfSquares(): Int = sumOf { it * it }

    private fun String.words(): List<String> = split(" ")

    private fun Person.info(): String = "$name is $age years old"

    private fun String.trimmed(): String = trim()

    private fun String.capitalized(): String = split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }

    private fun String.wrapped(prefix: String, suffix: String): String = "$prefix$this$suffix"

    // endregion

    // region Helper Classes (defined below the code that uses them)

    data class Person(val name: String, val age: Int)

    class SampleClass(private val value: String) {
        // This member function takes precedence over any extension with same signature
        fun describe(): String = "Member: $value"
    }

    open class Parent
    class Child : Parent()

    // Extensions for static resolution demo
    private fun Parent.getType(): String = "Parent"
    private fun Child.getType(): String = "Child"

    // This extension would be ignored because SampleClass has a member with same signature
    @Suppress("unused")
    private fun SampleClass.describe(): String = "Extension"

    // endregion
}
