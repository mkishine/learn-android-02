package com.example.sandbox

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

/**
 * Demonstrates Kotlin's property reference operator `::`.
 *
 * The `::` operator creates a reference to a property (or function), returning a `KProperty`
 * object that represents the property itself rather than its value. This enables reflection-like
 * capabilities at compile time with type safety.
 *
 * ## Summary Table
 *
 * | Test Method                          | Concept Demonstrated                                      |
 * |--------------------------------------|-----------------------------------------------------------|
 * | `boundPropertyReference`             | `::prop` on local/member creates bound reference          |
 * | `unboundPropertyReference`           | `Class::prop` creates unbound reference needing receiver  |
 * | `readingViaPropertyReference`        | Using `get()` to read property value                      |
 * | `writingViaMutablePropertyReference` | Using `set()` to write mutable property value             |
 * | `propertyReferenceAsArgument`        | Passing property references to higher-order functions     |
 * | `lateinitIsInitializedCheck`         | `::prop.isInitialized` checks lateinit state              |
 * | `propertyReferenceName`              | `KProperty.name` returns the property name as String      |
 */
@Suppress("MemberVisibilityCanBePrivate")
class PropertyReferenceTest {

    // region Bound Property References

    @Test
    fun boundPropertyReference() {
        // A "bound" property reference is bound to a specific instance.
        // Use instance::property syntax to bind to a specific object.

        // Given - a specific instance
        val person = Person("Alice", 30)

        // person::name creates a KProperty0<String> - bound to this specific instance
        // KProperty0 means "0 additional receivers needed" - it's already bound
        val propertyRef: KProperty0<String> = person::name

        // The reference is "bound" - it knows which instance to read from
        assertThat(propertyRef.get()).isEqualTo("Alice")
        assertThat(propertyRef.name).isEqualTo("name")
    }

    @Test
    fun boundPropertyReferenceOnInstance() {
        // Given - a specific instance
        val person = Person("Alice", 30)

        // When - we create a bound reference using instance::property syntax
        val nameRef: KProperty0<String> = person::name
        val ageRef: KProperty0<Int> = person::age

        // Then - the reference is bound to that specific instance
        assertThat(nameRef.get()).isEqualTo("Alice")
        assertThat(ageRef.get()).isEqualTo(30)
    }

    // endregion

    // region Unbound Property References

    @Test
    fun unboundPropertyReference() {
        // An "unbound" property reference is NOT bound to any instance.
        // Use ClassName::property syntax.
        // It requires an instance to be passed to get() or set().

        // Person::name creates a KProperty1<Person, String>
        // The "1" means it takes 1 receiver argument
        val nameRef: KProperty1<Person, String> = Person::name

        // Given - different instances
        val alice = Person("Alice", 30)
        val bob = Person("Bob", 25)

        // When - we use the same reference with different instances
        val aliceName = nameRef.get(alice)
        val bobName = nameRef.get(bob)

        // Then - we get values from the respective instances
        assertThat(aliceName).isEqualTo("Alice")
        assertThat(bobName).isEqualTo("Bob")
    }

    // endregion

    // region Reading and Writing via References

    @Test
    fun readingViaPropertyReference() {
        val person = Person("Charlie", 35)

        // Bound reference - no receiver needed
        val boundRef: KProperty0<String> = person::name
        assertThat(boundRef.get()).isEqualTo("Charlie")

        // Unbound reference - receiver required
        val unboundRef: KProperty1<Person, String> = Person::name
        assertThat(unboundRef.get(person)).isEqualTo("Charlie")
    }

    @Test
    fun writingViaMutablePropertyReference() {
        // Given - a class with a mutable property
        val counter = Counter(0)

        // KMutableProperty1 allows both get() and set()
        val valueRef: KMutableProperty1<Counter, Int> = Counter::value

        // When - we modify via the reference
        valueRef.set(counter, 42)

        // Then - the instance is updated
        assertThat(counter.value).isEqualTo(42)
        assertThat(valueRef.get(counter)).isEqualTo(42)
    }

    // endregion

    // region Property References as Arguments

    @Test
    fun propertyReferenceAsArgument() {
        // Property references are useful when a function expects a lambda
        // that extracts a value from an object.

        val people = listOf(
            Person("Alice", 30),
            Person("Bob", 25),
            Person("Charlie", 35)
        )

        // Instead of: people.map { it.name }
        // You can use: people.map(Person::name)
        val names = people.map(Person::name)
        val ages = people.sortedBy(Person::age).map(Person::name)

        assertThat(names).containsExactly("Alice", "Bob", "Charlie")
        assertThat(ages).containsExactly("Bob", "Alice", "Charlie")
    }

    @Test
    fun propertyReferenceWithCustomFunction() {
        // Given - a function that accepts a property reference
        val people = listOf(
            Person("Alice", 30),
            Person("Bob", 25)
        )

        // When - we pass a property reference
        val result = extractAll(people, Person::name)

        // Then
        assertThat(result).containsExactly("Alice", "Bob")
    }

    // endregion

    // region lateinit and isInitialized

    @Test
    fun lateinitIsInitializedCheck() {
        // The ::property.isInitialized extension is available only for lateinit properties.
        // It allows checking initialization state without triggering an exception.

        // Given
        val service = Service()

        // When - before initialization
        val beforeInit = service.isClientInitialized()

        // Then
        assertThat(beforeInit).isFalse()

        // When - after initialization
        service.client = "HttpClient"
        val afterInit = service.isClientInitialized()

        // Then
        assertThat(afterInit).isTrue()
        assertThat(service.client).isEqualTo("HttpClient")
    }

    // endregion

    // region Property Metadata

    @Test
    fun propertyReferenceName() {
        // KProperty.name returns the property name as a String.
        // Useful for logging, debugging, or building DSLs.

        val nameRef = Person::name
        val ageRef = Person::age

        assertThat(nameRef.name).isEqualTo("name")
        assertThat(ageRef.name).isEqualTo("age")
    }

    // endregion

    // region Helper Classes (defined below the code that uses them)

    data class Person(val name: String, val age: Int)

    data class Counter(var value: Int)

    class Service {
        lateinit var client: String

        fun isClientInitialized(): Boolean = ::client.isInitialized
    }

    private fun <T, R> extractAll(items: List<T>, property: KProperty1<T, R>): List<R> {
        return items.map { property.get(it) }
    }

    // endregion
}
