# Kotlin Learning Plan for Android Development

A structured path for Java developers transitioning to Kotlin for Android.

## Core Kotlin Concepts

### 1. Null Safety
Kotlin's type system distinguishes nullable types (`String?`) from non-nullable types (`String`). Key operators:
- `?` - declares a nullable type
- `?.` - safe call, returns null if receiver is null
- `?:` - Elvis operator, provides default when null
- `!!` - not-null assertion (use sparingly)
- Smart casts automatically cast after null checks

### 2. Extension Functions
Add new functions to existing classes without modifying them or using inheritance. Enables writing `"hello".capitalize()` even if `String` doesn't have that method. Foundation for creating fluent APIs.

### 3. Lambda Expressions
Anonymous functions with concise syntax. Key differences from Java:
- Single parameter implicitly named `it`
- Last lambda argument can be placed outside parentheses
- Return type is inferred
- Can capture and modify variables from enclosing scope

### 4. Higher-Order Functions
Functions that take other functions as parameters or return functions. Enables functional programming patterns like `map`, `filter`, `reduce`. Essential for writing reusable, composable code.

### 5. Lambda with Receiver
Combines extension functions with lambdas. Inside the lambda, you can call methods on the receiver without qualification. The core mechanism behind Kotlin DSLs. Example: `StringBuilder().apply { append("text") }`.

### 6. Data Classes
Classes that primarily hold data. The compiler automatically generates:
- `equals()` and `hashCode()`
- `toString()` with all properties
- `copy()` for creating modified copies
- `componentN()` functions for destructuring

### 7. Sealed Classes
Restricted class hierarchies where all subclasses are known at compile time. When used with `when` expressions, the compiler ensures all cases are handled. Ideal for representing states or results.

### 8. Object Declarations
Language-level support for singletons using `object` keyword. Companion objects provide static-like members for classes. Object expressions replace Java's anonymous inner classes.

### 9. Coroutines
Kotlin's solution for asynchronous programming. Lightweight threads that can be suspended without blocking. Key concepts:
- `suspend` functions
- Coroutine scopes and contexts
- `launch` and `async` builders
- Flow for reactive streams

### 10. Scope Functions
Five functions for executing code blocks on objects:
- `let` - transforms object, uses `it`
- `run` - transforms object, uses `this`
- `with` - like `run` but object passed as argument
- `apply` - configures object, returns it, uses `this`
- `also` - performs side effects, returns object, uses `it`

## Android-Specific Kotlin

### 1. Jetpack Compose
Modern declarative UI toolkit. UIs are built using composable functions that describe the UI based on current state. Heavily uses Kotlin DSL patterns for layout and styling.

### 2. ViewModels with Kotlin
Architecture component for UI-related data. Kotlin additions:
- `StateFlow` - observable state holder for UI state
- `SharedFlow` - for events that shouldn't be replayed
- `viewModelScope` - coroutine scope tied to ViewModel lifecycle

### 3. Kotlin Gradle DSL
Write build scripts in Kotlin instead of Groovy. Provides type safety, IDE support, and refactoring capabilities. Uses the same DSL patterns as other Kotlin code.

### 4. KTX Extensions
Official Android Kotlin extensions that make Android APIs more idiomatic. Examples: simplified fragment transactions, property delegates for shared preferences, coroutine integration.

## Recommended Learning Order

1. **Coroutines** - Appear in virtually all modern Android code
2. **Scope Functions** - Used constantly for object configuration
3. **Jetpack Compose** - The future of Android UI, builds on DSL knowledge
4. **ViewModels with Kotlin** - State management for Compose

## Progress Tracking

| Topic | Status | Notes |
|-------|--------|-------|
| Null Safety | | |
| Extension Functions | | |
| Lambda Expressions | | |
| Higher-Order Functions | | |
| Lambda with Receiver | Started | LambdaWithReceiverTest.kt |
| Data Classes | | |
| Sealed Classes | | |
| Object Declarations | | |
| Coroutines | | |
| Scope Functions | | |
| Jetpack Compose | | |
| ViewModels with Kotlin | | |
| Kotlin Gradle DSL | | |
| KTX Extensions | | |
