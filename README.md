# SparqTest

An Android application demonstrating modern Android development practices with a modular architecture based on Clean Architecture principles.

## Architecture

The application follows a three-layer architecture:

- **Data Layer**: Handles data operations and serves as a single source of truth
- **Domain Layer**: Contains business logic and use cases
- **Presentation Layer**: Handles UI components and user interactions

## Key Features

- **Clean Architecture**: Separation of concerns with clearly defined layers
- **Offline-First Approach**: Prioritizes local data and handles network errors gracefully
- **MVVM Pattern**: Separation of UI and business logic
- **Dependency Injection**: Using Hilt for DI throughout the app
- **Modern UI**: Built with Jetpack Compose
- **Pull-to-Refresh**: Integrated with Material 3 components
- **Unit Testing**: Comprehensive test coverage for ViewModel and Repository

## Technologies Used

- **Kotlin**: Modern language for Android development
- **Jetpack Compose**: Declarative UI toolkit
- **Room**: Local database for caching
- **Retrofit**: Type-safe HTTP client for API communication
- **Hilt**: Dependency injection library
- **Kotlin Coroutines & Flow**: Asynchronous programming
- **Material 3**: Modern design system components

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Build and run the application

## Testing

The application includes unit tests for the ViewModel and Repository implementations. Run the tests using:

```
./gradlew test
```

## License

[Insert License Information Here] 