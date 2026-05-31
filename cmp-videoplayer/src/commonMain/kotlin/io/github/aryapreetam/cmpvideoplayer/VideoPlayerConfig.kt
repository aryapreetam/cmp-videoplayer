package io.github.aryapreetam.cmpvideoplayer

import androidx.compose.runtime.compositionLocalOf

public enum class LifecyclePolicy {
  /** Choose the platform’s default behavior. */
  PlatformDefault,

  /** Pause when the app is backgrounded (or equivalent). */
  PauseOnBackground,

  /** Only pause/release on disposal; ignore app backgrounding. */
  PauseOnDisposeOnly,
}

public enum class RenderMode {
  /** Render the real platform player. */
  Platform,

  /** Render a pure-Compose placeholder only (no interop views/players). */
  PlaceholderOnly,
}

public data class VideoPlayerConfig(
  val showControls: Boolean = true,
  val autoplay: Boolean = false,
  val loop: Boolean = false,
  val exclusivePlayback: Boolean = true,
  val lifecyclePolicy: LifecyclePolicy = LifecyclePolicy.PlatformDefault,
  val renderMode: RenderMode = RenderMode.Platform,
)

public val LocalVideoPlayerConfig = compositionLocalOf { VideoPlayerConfig() }
