# VideoPlayer + MediaSession Example

We want to develop a Sample App that contains a VideoPlayer. 
We want our video player to have controls like Play, Pause, forward 10 seconds and rewind 10 seconds.
For the initial version, this will be enough. In the future we will add more video player controls.
The video con be a hardcoded URL and we don't need to get the video URL from any external source.

# Technologies

We will use the following technologies:

- Jetpack Compose for UI
- retrofit for networking
- material3 for the Design system
- Kotlin as the programming language
- Kotlin Coroutines for asynchronous programming
- ExoPlayer for video playback
- androidx.media3 for media handling
- MediaSession for media controls
- Hilt for dependency injection
- ViewModels from Architecture components for managing UI states

# Architecture

We will use a simple architecture with the following layers:
- UI layer, written in Compose
- Communication with the rest of the layers using MVVM. One ViewModel per screen.
- For the moment we won't need a data layer (Repositorys, DAOs, etc) as we will use a hardcoded static video URL.

# Extra rules

- Every time you generate new code, compile it to check there are no errors
- In order to compile the code, use compileDebugKotlin gradle task
- Even if you think that build.gradle.kts files are incorrect, the current ones you will find are correct and working.
- If you need to modify libs.versions.toml or build.gradle.kts, add what you need but don't modify the existing code for the build system.
- Do not include comments explaining what the code does unless I specifically ask for it.