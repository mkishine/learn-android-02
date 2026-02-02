package com.example.sandbox

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
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
 */
class NullSafetyTest {

    // region Nullable vs Non-Nullable Types

    @Test
    fun `nullableVsNonNullableTypes`() {
        // Non-nullable type - cannot hold null
        val nonNullable: String = "Hello"

        // Nullable type - can hold null (note the ?)
        val nullable: String? = null
        val nullableWithValue: String? = "World"

        assertEquals("Hello", nonNullable)
        assertNull(nullable)
        assertEquals("World", nullableWithValue)
    }

    // endregion

    // region Safe Call Operator (?.)

    @Test
    fun `safeCallOperator`() {
        // Given
        val nullString: String? = null
        val nonNullString: String? = "Hello"

        // When - safe call returns null if receiver is null
        val nullLength: Int? = nullString?.length
        val nonNullLength: Int? = nonNullString?.length

        // Then
        assertNull(nullLength)
        assertEquals(5, nonNullLength)
    }

    @Test
    fun `safeCallChaining`() {
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
        assertEquals("NEW YORK", city1)
        assertNull(city2)
        assertNull(city3)
    }

    // endregion

    // region Elvis Operator (?:)

    @Test
    fun `elvisOperatorProvidesDefault`() {
        // Given
        val nullValue: String? = null
        val nonNullValue: String? = "Hello"

        // When - Elvis operator provides default when null
        val result1 = nullValue ?: "Default"
        val result2 = nonNullValue ?: "Default"

        // Then
        assertEquals("Default", result1)
        assertEquals("Hello", result2)
    }

    @Test
    fun `elvisOperatorWithThrow`() {
        // Given
        val nullValue: String? = null

        // When/Then - Elvis can throw exception as default action
        assertThrows(IllegalArgumentException::class.java) {
            nullValue ?: throw IllegalArgumentException("Value cannot be null")
        }
    }

    // endregion

    // region Not-Null Assertion (!!)

    @Test
    fun `notNullAssertionSuccess`() {
        // Given
        val nullable: String? = "Hello"

        // When - !! asserts the value is not null
        val nonNull: String = nullable!!

        // Then
        assertEquals("Hello", nonNull)
    }

    @Test
    fun `notNullAssertionThrowsOnNull`() {
        // Given
        val nullable: String? = null

        // When/Then - !! throws NullPointerException if value is null
        assertThrows(NullPointerException::class.java) {
            @Suppress("ALWAYS_NULL")
            nullable!!
        }
    }

    // endregion

    // region Smart Casts

    @Test
    fun `smartCastAfterNullCheck`() {
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
        assertEquals(5, result)
    }

    @Test
    fun `smartCastInWhenExpression`() {
        // Given
        val nullable: String? = "Kotlin"

        // When - smart cast works in when expressions
        val result = when {
            nullable == null -> "null"
            nullable.isEmpty() -> "empty"  // Smart cast to String here
            else -> "length: ${nullable.length}"  // And here
        }

        // Then
        assertEquals("length: 6", result)
    }

    // endregion

    // region Safe Call with let

    @Test
    fun `letWithSafeCall`() {
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
        assertEquals(false, nullExecuted)
        assertEquals(true, nonNullExecuted)
    }

    // endregion

    // region Nullable Collections

    @Test
    fun `nullableCollectionElements`() {
        // Given - list can contain nullable elements
        val listWithNulls: List<String?> = listOf("A", null, "B", null, "C")

        // When
        val firstNull = listWithNulls[1]
        val nonNullCount = listWithNulls.count { it != null }

        // Then
        assertNull(firstNull)
        assertEquals(3, nonNullCount)
    }

    @Test
    fun `filterNotNullOnCollections`() {
        // Given
        val listWithNulls: List<String?> = listOf("A", null, "B", null, "C")

        // When - filterNotNull removes all null elements and returns List<String>
        val filtered: List<String> = listWithNulls.filterNotNull()

        // Then
        assertEquals(listOf("A", "B", "C"), filtered)
    }

    // endregion
}