package io.github.aryapreetam.cmpvideoplayer

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

internal actual fun isVideoSourceSupported(source: VideoSource): Boolean = true

private object ActiveExoPlayerRegistry {
  private var active: ExoPlayer? = null

  fun setActive(player: ExoPlayer) {
    val previous = active
    if (previous != null && previous !== player) {
      previous.pause()
    }
    active = player
  }

  fun clear(player: ExoPlayer) {
    if (active === player) {
      active = null
    }
  }
}

@OptIn(UnstableApi::class)
@Composable
internal actual fun PlatformVideoPlayer(
  source: VideoSource,
  modifier: Modifier,
  config: VideoPlayerConfig,
  onError: () -> Unit,
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val uri = remember(source) {
    when (source) {
      is VideoSource.Path -> Uri.fromFile(File(source.path))
      is VideoSource.Url -> Uri.parse(source.url)
    }
  }

  val exoPlayer = remember(uri) {
    ExoPlayer.Builder(context).build().apply {
      setMediaItem(ExoMediaItem.fromUri(uri))
      repeatMode = if (config.loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
      playWhenReady = config.autoplay
      prepare()
    }
  }

  LaunchedEffect(config.autoplay) {
    exoPlayer.playWhenReady = config.autoplay
    if (config.autoplay) {
      exoPlayer.play()
    } else {
      exoPlayer.pause()
    }
  }

  LaunchedEffect(config.loop) {
    exoPlayer.repeatMode = if (config.loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
  }

  DisposableEffect(exoPlayer) {
    onDispose {
      ActiveExoPlayerRegistry.clear(exoPlayer)
      exoPlayer.release()
    }
  }

  DisposableEffect(exoPlayer, config.exclusivePlayback) {
    if (!config.exclusivePlayback) {
      return@DisposableEffect onDispose { }
    }

    val listener = object : Player.Listener {
      override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
          ActiveExoPlayerRegistry.setActive(exoPlayer)
        }
      }
    }
    exoPlayer.addListener(listener)
    onDispose {
      exoPlayer.removeListener(listener)
    }
  }

  DisposableEffect(lifecycleOwner, exoPlayer, config.lifecyclePolicy) {
    if (config.lifecyclePolicy == LifecyclePolicy.PauseOnDisposeOnly) {
      return@DisposableEffect onDispose { }
    }

    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_PAUSE) {
        exoPlayer.pause()
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  AndroidView(
    factory = { ctx ->
      PlayerView(ctx).apply {
        player = exoPlayer
        useController = config.showControls
        layoutParams = FrameLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
      }
    },
    modifier = modifier,
    update = { view ->
      try {
        view.useController = config.showControls
      } catch (_: Throwable) {
        onError()
      }
    }
  )
}
