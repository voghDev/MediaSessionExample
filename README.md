# MediaSessionExample for Android

A modern Android video player application built with Jetpack Compose and ExoPlayer, demonstrating media playback controls and MediaSession integration.

![Video Player Screenshot 1](https://github.com/user-attachments/assets/d6ce181c-3fd1-4304-a728-1e531c29c324)
![Video Player Screenshot 2](https://github.com/user-attachments/assets/7f3ad573-def3-4ac6-81c2-d718cd13da14)

---

## Overview

This repository contains an Android application that showcases a fully functional video player with modern UI controls and media session integration. The app demonstrates best practices for video playback in Android using ExoPlayer and Jetpack Compose.

## Features

### Video Playback
- **High-quality video streaming** using ExoPlayer
- **Adaptive streaming** support for optimal playback experience
- **Buffering indicators** with loading states
- **Seamless playback** with proper lifecycle management

### Media Controls
- **Play/Pause functionality** with intuitive button states
- **Forward 10 seconds** - Quick skip ahead control
- **Rewind 10 seconds** - Quick skip back control
- **Seek functionality** with interactive progress slider
- **Time display** showing current position and total duration
- **Custom overlay controls** with Material Design 3

### Technical Features
- **MVVM Architecture** with ViewModels managing player state
- **Reactive UI updates** using StateFlow
- **Proper resource management** with automatic player cleanup
- **Landscape orientation** optimized for video viewing
- **Material 3 design system** integration

## Technology Stack

This application is built using modern Android development technologies:

- **Jetpack Compose** - Modern declarative UI toolkit
- **ExoPlayer (Media3)** - Advanced media playback library
- **Material 3** - Latest Material Design system
- **Kotlin** - Primary programming language
- **Kotlin Coroutines** - Asynchronous programming
- **ViewModels** - Architecture components for UI state management
- **StateFlow** - Reactive state management

### Dependencies

```kotlin
// Media playback
androidx.media3:media3-exoplayer:1.2.1
androidx.media3:media3-ui:1.2.1
androidx.media3:media3-common:1.2.1

// UI and Compose
androidx.compose.material:material-icons-extended:1.6.7
androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0
```

## Architecture

The application follows a clean, layered architecture:

### UI Layer
- **Jetpack Compose** screens with declarative UI
- **Material 3** components for consistent design
- **Custom video controls** overlay

### Presentation Layer
- **MVVM pattern** with ViewModels per screen
- **StateFlow** for reactive state management
- **Lifecycle-aware** components

### Media Layer
- **ExoPlayer integration** for video playback
- **MediaSession** for media controls (future enhancement)
- **Proper resource management** and cleanup

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24 (API level 24) or higher
- Kotlin 1.9.0 or later

### Building the Project

1. **Clone the repository**
   ```bash
   git clone https://github.com/voghDev/MediaSessionExample.git
   cd MediaSessionExample
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Compile and check for errors**
   ```bash
   ./gradlew compileDebugKotlin
   ```

### Running the Application

1. **Connect an Android device** or start an emulator
2. **Run the app** from Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

### Code Quality

This project includes ktlint for code formatting and linting:

```bash
# Check code style
./gradlew ktlintCheck

# Auto-format code
./gradlew ktlintFormat
```

## Project Structure

```
app/src/main/java/es/voghdev/mediasessionexample/
├── MainActivity.kt                 # Main entry point
├── VideoPlayerViewModel.kt         # ViewModel managing player state
├── VideoPlayerScreen.kt           # Compose UI for video player
└── ui/theme/                      # Material 3 theme configuration
```

## Future Enhancements

- **MediaSession integration** for system-level media controls
- **Playlist support** for multiple video playback
- **Network video loading** with URL input
- **Picture-in-Picture mode** support
- **Subtitle support** and accessibility features
- **Video quality selection** controls

## Development Guidelines

- **Compile before committing** - Always run `./gradlew compileDebugKotlin`
- **Follow ktlint rules** - Use `./gradlew ktlintFormat` for consistent formatting
- **MVVM architecture** - One ViewModel per screen
- **Compose-first** - Use Jetpack Compose for all UI components
- **Material 3** - Follow Material Design 3 guidelines

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Built with ❤️ using Jetpack Compose and ExoPlayer**
