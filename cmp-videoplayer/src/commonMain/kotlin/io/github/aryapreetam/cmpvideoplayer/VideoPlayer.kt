package io.github.aryapreetam.cmpvideoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics

@Composable
public fun VideoPlayer(
  source: VideoSource,
  modifier: Modifier = Modifier,
  config: VideoPlayerConfig = LocalVideoPlayerConfig.current,
  placeholder: (@Composable () -> Unit)? = null,
) {
  var hasError by remember(source, config.renderMode) { mutableStateOf(false) }
  val supported = remember(source) { isVideoSourceSupported(source) }

  val rootModifier = Modifier
    .testTag(VideoPlayerTestTags.Root)
    .then(modifier)
    .semantics {
      isCmpVideoPlayer = true
      cmpVideoPlayerHasError = hasError || !supported
    }

  Box(modifier = rootModifier) {
    val shouldRenderPlaceholder = config.renderMode == RenderMode.PlaceholderOnly || !supported

    if (shouldRenderPlaceholder) {
      if (placeholder != null) {
        placeholder()
      } else {
        Box(Modifier.fillMaxSize().background(Color.Black))
      }
      return@Box
    }

    PlatformVideoPlayer(
      source = source,
      modifier = Modifier.fillMaxSize(),
      config = config,
      onError = { hasError = true }
    )

    if (hasError) {
      if (placeholder != null) {
        placeholder()
      } else {
        Box(Modifier.fillMaxSize().background(Color.Black))
      }
    }
  }
}

internal expect fun isVideoSourceSupported(source: VideoSource): Boolean

@Composable
internal expect fun PlatformVideoPlayer(
  source: VideoSource,
  modifier: Modifier,
  config: VideoPlayerConfig,
  onError: () -> Unit,
)
