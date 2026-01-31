# Project Summary: HelloWorld

This document summarizes the key aspects of the HelloWorld Android project.

## Overview

The project is a simple "Hello World" application built using Jetpack Compose. It's a single-activity app that displays the text "Hello World!" in the center of the screen.

## Project Structure

- **Root directory**: `/home/mkishine/Projects/learn-android-02`
- **App module**: `:app`
- **Main Activity**: `com.example.helloworld.MainActivity.kt`

## How it works

1.  The `MainActivity` is the entry point of the application.
2.  In its `onCreate` method, it uses `setContent` to build the UI with Jetpack Compose.
3.  The UI is defined by the `Greeting` composable function, which displays a `Text` element inside a `Box` for centering.
4.  The project also includes a `@Preview` composable (`GreetingPreview`) for easy UI development and previewing within Android Studio.

## Theming

The application uses a custom theme called `HelloWorldTheme` to ensure a consistent visual appearance.

- **Location**: `app/src/main/java/com/example/helloworld/ui/theme/Theme.kt`
- **Functionality**:
    - It wraps the application's content in `MaterialTheme`.
    - It supports both light and dark color schemes, automatically switching based on the system setting.
    - It supports dynamic color on Android 12+ devices, adapting to the user's wallpaper.
    - It styles the system status bar to match the app's theme.
