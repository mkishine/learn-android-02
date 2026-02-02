package com.example.sandbox

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

/**
 * We are learning Kotlin concepts important to understanding Kotlin-based Android Applications.
 * In this test class we will focus on Null Safety.
 *
 * Kotlin's type system distinguishes nullable types (`String?`) from non-nullable types (`String`).
 *
 * ## Summary Table
 *
 * | Test Method                          | Concept Demonstrated                              |
 * |--------------------------------------|---------------------------------------------------|
 * | `nullableVsNonNullableTypes`         | Declaring nullable (`?`) vs non-nullable types    |
 * | `safeCallOperator`                   | `?.` returns null if receiver is null             |
 * | `safeCallChaining`                   | Chaining multiple `?.` calls                      |
 * | `elvisOperatorProvidesDefault`       | `?:` provides default value when null             |
 * | `elvisOperatorWithThrow`             | `?:` can throw exception as default action        |
 * | `notNullAssertionSuccess`            | `!!` asserts value is not null                    |
 * | `notNullAssertionThrowsOnNull`       | `!!` throws NullPointerException when null        |
 * | `smartCastAfterNullCheck`            | Compiler smart casts after null check             |
 * | `smartCastInWhenExpression`          | Smart cast works in `when` expressions            |
 * | `letWithSafeCall`                    | `?.let {}` executes block only if not null        |
 * | `nullableCollectionElements`         | Collections can contain nullable elements         |
 * | `filterNotNullOnCollections`         | `filterNotNull()` removes nulls from collections  |
 * | `safeCastOperator`                   | `as?` returns null instead of throwing on failed cast |
 * | `lateinitModifier`                   | `lateinit` defers initialization of non-null var  |
 * | `isNullOrEmptyAndIsNullOrBlank`      | Null-safe string checks                           |
 */
@Suppress("RedundantNullableReturnType", "KotlinConstantConditions", "RedundantExplicitType",
    "IfThenToElvis"
)
class NullSafetyTest {

    // region Nullable vs Non-Nullable Types

    @Test
    fun nullableVsNonNullableTypes() {
        // Non-nullable type - cannot hold null
        val nonNullable: String = "Hello"

        // Nullable type - can hold null (note the ?)
        val nullable: String? = null
        val nullableWithValue: String? = "World"

        assertThat(nonNullable).isEqualTo("Hello")
        assertThat(nullable).isNull()
        assertThat(nullableWithValue).isEqualTo("World")
    }

    // endregion

    // region Safe Call Operator (?.)

    @Test
    fun safeCallOperator() {
        // Given
        // Remember, Michael, '?' is attached to the String. '?' is not part of the variable name.
        val nullString: String? = null
        val nonNullString: String? = "Hello"

        // When - safe call returns null if receiver is null
        val nullLength: Int? = nullString?.length
        val nonNullLength: Int? = nonNullString?.length

        // Then
        assertThat(nullLength).isNull()
        assertThat(nonNullLength).isEqualTo(5)
    }

    @Test
    fun safeCallChaining() {
        // Given
        data class Address(val city: String?)
        data class Person(val address: Address?)

        val personWithCity = Person(Address("New York"))
        val personWithNullCity = Person(Address(null))
        val personWithNullAddress: Person? = null

        // When - chaining safe calls
        val city1 = personWithCity.address?.city?.uppercase()
        val city2 = personWithNullCity.address?.city?.uppercase()
        val city3 = personWithNullAddress?.address?.city?.uppercase()

        // Then - entire chain returns null if any part is null
        assertThat(city1).isEqualTo("NEW YORK")
        assertThat(city2).isNull()
        assertThat(city3).isNull()
    }

    // endregion

    // region Elvis Operator (?:)

    @Test
    fun elvisOperatorProvidesDefault() {
        // Given
        val nullValue: String? = null
        val nonNullValue: String? = "Hello"

        // When - Elvis operator provides default when null
        val result1 = nullValue ?: "Default"
        val result2 = nonNullValue ?: "Default"

        // Then
        assertThat(result1).isEqualTo("Default")
        assertThat(result2).isEqualTo("Hello")
    }

    @Test
    fun elvisOperatorWithThrow() {
        // Given
        val nullValue: String? = null

        // When/Then - Elvis can throw exception as default action
        assertThatThrownBy {
            nullValue ?: throw IllegalArgumentException("Value cannot be null")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    // endregion

    // region Not-Null Assertion (!!)

    @Test
    fun notNullAssertionSuccess() {
        // Given
        val nullable: String? = "Hello"

        // When - !! asserts the value is not null
        val nonNull: String = nullable!!

        // Then
        assertThat(nonNull).isEqualTo("Hello")
    }

    @Test
    fun notNullAssertionThrowsOnNull() {
        // Given
        val nullable: String? = null

        // When/Then - !! throws NullPointerException if value is null
        assertThatThrownBy {
            @Suppress("ALWAYS_NULL")
            nullable!!
        }.isInstanceOf(NullPointerException::class.java)
    }

    // endregion

    // region Smart Casts

    @Test
    fun smartCastAfterNullCheck() {
        // Given
        val nullable: String? = "Hello"

        // When - after null check, compiler smart casts to non-nullable
        val result = if (nullable != null) {
            // Inside this block, nullable is smart cast to String (non-nullable)
            nullable.length  // No need for ?. or !!
        } else {
            -1
        }

        // Then
        assertThat(result).isEqualTo(5)
    }

    @Test
    fun smartCastInWhenExpression() {
        // Given
        val nullable: String? = "Kotlin"

        // When - smart cast works in when expressions
        val result = when {
            nullable == null -> "null"
            nullable.isEmpty() -> "empty"  // Smart cast to String here
            else -> "length: ${nullable.length}"  // And here
        }

        // Then
        assertThat(result).isEqualTo("length: 6")
    }

    // endregion

    // region Safe Call with let

    @Test
    fun letWithSafeCall() {
        // Given
        val nullValue: String? = null
        val nonNullValue: String? = "Hello"
        var nullExecuted = false
        var nonNullExecuted = false

        // When - ?.let executes block only if not null
        nullValue?.let {
            nullExecuted = true
        }
        nonNullValue?.let {
            nonNullExecuted = true
        }

        // Then
        assertThat(nullExecuted).isFalse()
        assertThat(nonNullExecuted).isTrue()
    }

    // endregion

    // region Nullable Collections

    @Test
    fun nullableCollectionElements() {
        // Given - list can contain nullable elements
        val listWithNulls: List<String?> = listOf("A", null, "B", null, "C")

        // When
        val firstNull = listWithNulls[1]
        val nonNullCount = listWithNulls.count { it != null }

        // Then
        assertThat(firstNull).isNull()
        assertThat(nonNullCount).isEqualTo(3)
    }

    @Test
    fun filterNotNullOnCollections() {
        // Given
        val listWithNulls: List<String?> = listOf("A", null, "B", null, "C")

        // When - filterNotNull removes all null elements and returns List<String>
        val filtered: List<String> = listWithNulls.filterNotNull()

        // Then
        assertThat(filtered).containsExactly("A", "B", "C")
    }

    // endregion

    // region Safe Cast Operator (as?)

    @Test
    fun safeCastOperator() {
        // Given
        val anyString: Any = "Hello"
        val anyInt: Any = 42

        // When - as? returns null instead of throwing ClassCastException
        val successfulCast: String? = anyString as? String
        val failedCast: String? = anyInt as? String

        // Then
        assertThat(successfulCast).isEqualTo("Hello")
        assertThat(failedCast).isNull()
    }

    // endregion

    // region lateinit Modifier
    //
    // `lateinit` allows declaring a non-null var without immediate initialization.
    // The compiler trusts you to initialize it before first use.
    //
    // Key points:
    // - Avoids making the property nullable just because it's initialized later
    // - Accessing before initialization throws UninitializedPropertyAccessException
    // - Can check with `::property.isInitialized`
    // - Only for var (not val), non-primitive, non-nullable types
    //
    // Common use cases:
    // - Android views initialized in onCreate()
    // - Dependency injection
    // - Test fixtures in @Before methods

    @Test
    fun lateinitModifier() {
        // Given - a class with lateinit property (common in Android for views)
        class Controller {
            lateinit var name: String

            fun isNameInitialized(): Boolean = ::name.isInitialized
        }

        val controller = Controller()

        // When - before initialization
        val beforeInit = controller.isNameInitialized()

        // Then - lateinit var is not yet initialized
        assertThat(beforeInit).isFalse()

        // When - after initialization
        controller.name = "MyController"
        val afterInit = controller.isNameInitialized()

        // Then - now it's initialized and can be used
        assertThat(afterInit).isTrue()
        assertThat(controller.name).isEqualTo("MyController")
    }

    // endregion

    // region Null-Safe String Checks

    @Test
    fun isNullOrEmptyAndIsNullOrBlank() {
        // Given
        val nullString: String? = null
        val emptyString: String? = ""
        val blankString: String? = "   "
        val normalString: String? = "Hello"

        // When/Then - isNullOrEmpty checks for null or empty string
        assertThat(nullString.isNullOrEmpty()).isTrue()
        assertThat(emptyString.isNullOrEmpty()).isTrue()
        assertThat(blankString.isNullOrEmpty()).isFalse()  // whitespace is not empty
        assertThat(normalString.isNullOrEmpty()).isFalse()

        // When/Then - isNullOrBlank also considers whitespace-only as blank
        assertThat(nullString.isNullOrBlank()).isTrue()
        assertThat(emptyString.isNullOrBlank()).isTrue()
        assertThat(blankString.isNullOrBlank()).isTrue()   // whitespace IS blank
        assertThat(normalString.isNullOrBlank()).isFalse()
    }

    // endregion
}