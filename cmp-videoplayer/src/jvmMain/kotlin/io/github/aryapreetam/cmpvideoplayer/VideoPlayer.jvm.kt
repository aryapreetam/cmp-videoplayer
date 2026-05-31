package io.github.aryapreetam.cmpvideoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openani.mediamp.PlaybackState
import org.openani.mediamp.compose.MediampPlayerSurface
import org.openani.mediamp.compose.rememberMediampPlayer
import org.openani.mediamp.playUri
import java.io.File

internal actual fun isVideoSourceSupported(source: VideoSource): Boolean = true

private object ActiveMediampRegistry {
  private var activeKey: String? = null
  private var pauseActive: (() -> Unit)? = null

  fun setActive(key: String, pause: () -> Unit) {
    if (activeKey != null && activeKey != key) {
      pauseActive?.invoke()
    }
    activeKey = key
    pauseActive = pause
  }

  fun clear(key: String) {
    if (activeKey == key) {
      activeKey = null
      pauseActive = null
    }
  }
}

@Composable
internal actual fun PlatformVideoPlayer(
  source: VideoSource,
  modifier: Modifier,
  config: VideoPlayerConfig,
  onError: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val focusRequester = remember { FocusRequester() }

  val sourceKey = remember(source) {
    when (source) {
      is VideoSource.Path -> "path:${source.path}"
      is VideoSource.Url -> "url:${source.url}"
    }
  }

  val playableUri = remember(source) {
    when (source) {
      is VideoSource.Path -> File(source.path).toURI().toString()
      is VideoSource.Url -> source.url
    }
  }

  val player = rememberMediampPlayer()
  val playbackState by player.playbackState.collectAsState()
  val currentPosition by player.currentPositionMillis.collectAsState()
  val mediaProperties by player.mediaProperties.collectAsState()

  val duration = mediaProperties?.durationMillis ?: 0L
  val isPlaying = playbackState.isPlaying
  val isBuffering = playbackState == PlaybackState.PAUSED_BUFFERING

  var hasStartedPlayback by remember(sourceKey) { mutableStateOf(false) }
  var showControls by remember { mutableStateOf(true) }
  var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }

  fun onInteraction() {
    showControls = true
    lastInteractionTime = System.currentTimeMillis()
  }

  fun pause() {
    player.pause()
  }

  fun startOrResumePlayback() {
    onInteraction()
    if (config.exclusivePlayback) {
      ActiveMediampRegistry.setActive(sourceKey) { player.pause() }
    }
    when {
      playbackState == PlaybackState.READY || playbackState == PlaybackState.PAUSED -> player.resume()
      isPlaying -> player.pause()
      else -> {
        hasStartedPlayback = true
        scope.launch {
          try {
            player.playUri(playableUri)
          } catch (_: Throwable) {
            onError()
          }
        }
      }
    }
  }

  LaunchedEffect(lastInteractionTime, isPlaying) {
    if (isPlaying && config.showControls) {
      delay(3000)
      if (System.currentTimeMillis() - lastInteractionTime >= 3000) {
        showControls = false
      }
    }
  }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  LaunchedEffect(sourceKey, config.autoplay) {
    if (config.autoplay) {
      startOrResumePlayback()
    } else {
      pause()
    }
  }

  LaunchedEffect(config.loop, playbackState) {
    if (!config.loop) return@LaunchedEffect
    if (playbackState.name == "ENDED") {
      player.seekTo(0)
      player.resume()
    }
  }

  DisposableEffect(sourceKey) {
    onDispose {
      ActiveMediampRegistry.clear(sourceKey)
      player.pause()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .focusRequester(focusRequester)
      .focusable()
      .onKeyEvent { event ->
        if (event.type == KeyEventType.KeyUp) {
          when (event.key) {
            Key.Spacebar -> {
              startOrResumePlayback()
              true
            }

            Key.DirectionLeft -> {
              onInteraction()
              player.seekTo(maxOf(0, currentPosition - 10_000))
              true
            }

            Key.DirectionRight -> {
              onInteraction()
              player.seekTo(minOf(duration, currentPosition + 10_000))
              true
            }

            else -> false
          }
        } else {
          false
        }
      }
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
      ) {
        startOrResumePlayback()
      },
    contentAlignment = Alignment.Center
  ) {
    MediampPlayerSurface(
      mediampPlayer = player,
      modifier = Modifier.fillMaxSize(),
    )

    if (hasStartedPlayback && (isBuffering || (!isPlaying && duration == 0L && playbackState != PlaybackState.PAUSED))) {
      CircularProgressIndicator(
        color = Color.White,
        modifier = Modifier.size(48.dp)
      )
    }

    val shouldShowChrome = config.showControls && ((showControls || !isPlaying) && (duration > 0 || hasStartedPlayback))

    if (shouldShowChrome) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
            )
          )
          .padding(16.dp)
      ) {
        Column {
          if (duration > 0) {
            Slider(
              value = currentPosition.toFloat(),
              onValueChange = { newValue ->
                onInteraction()
                player.seekTo(newValue.toLong())
              },
              valueRange = 0f..duration.toFloat(),
              colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
              ),
              modifier = Modifier.fillMaxWidth()
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = { startOrResumePlayback() }) {
              Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
              )
            }

            if (duration > 0) {
              Text(
                text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                color = Color.White,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    }
  }
}

private fun formatTime(millis: Long): String {
  val totalSeconds = millis / 1000
  val hours = totalSeconds / 3600
  val minutes = (totalSeconds % 3600) / 60
  val seconds = totalSeconds % 60

  return if (hours > 0) {
    String.format("%d:%02d:%02d", hours, minutes, seconds)
  } else {
    String.format("%d:%02d", minutes, seconds)
  }
}
