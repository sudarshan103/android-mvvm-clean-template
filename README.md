# MvvmCleanTemplate

Clean Architecture Android template with MVVM, Hilt DI, Retrofit, and Compose UI. Maintains Android 7 (API 24) compatibility with TLS 1.2 support for SSL.

## Modules
- `app`: Presentation layer (Compose UI, ViewModels, Hilt entry points)
- `domain`: Business logic, use cases, and domain models
- `data`: Data sources, Retrofit client, repository implementations

## Features
- MVVM with Clean Architecture boundaries
- Hilt dependency injection
- Retrofit + OkHttp networking
- TLS 1.2 socket support for Android 7 devices
- Compose UI with Material 3
- Comprehensive unit testing with MockK
- 85% code coverage enforcement with Kover

## Requirements
- Android Studio (latest stable recommended)
- JDK 11
- Android SDK 24+

## Build
```zsh
./gradlew :app:assembleDebug
```

## Run
- Open in Android Studio
- Select `app` configuration
- Run on emulator or device (API 24+)

## Testing
Run all unit tests:
```zsh
./gradlew test
```

Run tests with coverage verification (85% threshold):
```zsh
./gradlew test koverVerify
```

Generate HTML coverage reports:
```zsh
./gradlew koverHtmlReport
```

View coverage reports:
- App: `app/build/reports/kover/html/index.html`
- Data: `data/build/reports/kover/html/index.html`
- Domain: `domain/build/reports/kover/html/index.html`

## Quality Checks

Run Detekt static analysis:
```zsh
./gradlew detekt
# or use: make detekt
```

Run KtLint formatting check:
```zsh
./gradlew ktlintCheck
# or use: make ktlint
```

Run full quality check (clean + detekt + tests + coverage):
```zsh
./gradlew clean detekt test koverVerify
# or use: make quality
```

## Notes
- The `app` module depends on `data` only for DI wiring. App code should use domain interfaces.
- SSL permissive mode is intended for development only.

## Project Structure
```
app/
data/
domain/
```

## License
Specify a license before publishing. Common choices:
- MIT
- Apache-2.0
- BSD-3-Clause

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a pull request

