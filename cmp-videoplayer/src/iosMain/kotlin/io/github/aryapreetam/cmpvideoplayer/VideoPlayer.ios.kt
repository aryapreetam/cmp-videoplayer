package io.github.aryapreetam.cmpvideoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.UIKit.UIColor
import platform.UIKit.UIView
import kotlin.native.concurrent.ThreadLocal

internal actual fun isVideoSourceSupported(source: VideoSource): Boolean = true

@ThreadLocal
private object ActivePlayerRegistry {
  var activeKey: String? = null
    private set
  var pauseActive: (() -> Unit)? = null
    private set

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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun PlatformVideoPlayer(
  source: VideoSource,
  modifier: Modifier,
  config: VideoPlayerConfig,
  onError: () -> Unit,
) {
  val configState = rememberUpdatedState(config)
  val onErrorState = rememberUpdatedState(onError)

  val sourceKey = remember(source) {
    when (source) {
      is VideoSource.Path -> "path:${source.path}"
      is VideoSource.Url -> "url:${source.url}"
    }
  }

  val nsUrl = remember(sourceKey) { toNsUrlOrNull(source) }
  val holder = remember(sourceKey) { nsUrl?.let { IosPlayerHolder(it) } }

  if (nsUrl == null || holder == null) {
    LaunchedEffect(sourceKey) {
      onErrorState.value.invoke()
    }
    return
  }

  LaunchedEffect(sourceKey, config.autoplay) {
    if (config.autoplay) {
      if (config.exclusivePlayback) {
        ActivePlayerRegistry.setActive(sourceKey) { holder.pause() }
      }
      holder.play()
    } else {
      holder.pause()
    }
  }

  DisposableEffect(sourceKey, config.lifecyclePolicy, config.loop, config.exclusivePlayback) {
    val center = NSNotificationCenter.defaultCenter

    val lifecycleTokens = buildList {
      if (config.lifecyclePolicy != LifecyclePolicy.PauseOnDisposeOnly) {
        add(
          center.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = null,
          ) { _: NSNotification? ->
            holder.pause()
          }
        )
        add(
          center.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = null,
          ) { _: NSNotification? ->
            holder.pause()
          }
        )
      }
    }

    val loopToken = if (config.loop) {
      // Note: Kotlin/Native platform stubs differ across versions; we avoid KVO and seek APIs.
      // Instead, when playback reaches the end we replace the item with a fresh one.
      center.addObserverForName(
        name = platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification,
        `object` = null,
        queue = null,
      ) { _: NSNotification? ->
        if (configState.value.loop) {
          // Preserve exclusive playback semantics.
          if (configState.value.exclusivePlayback) {
            ActivePlayerRegistry.setActive(sourceKey) { holder.pause() }
          }
          holder.restartAndPlay()
        }
      }
    } else {
      null
    }

    onDispose {
      ActivePlayerRegistry.clear(sourceKey)
      lifecycleTokens.forEach(center::removeObserver)
      if (loopToken != null) center.removeObserver(loopToken)
      holder.dispose()
    }
  }

  UIKitView(
    factory = {
      holder.ensureView().apply {
        backgroundColor = UIColor.blackColor
      }
    },
    modifier = modifier,
    update = {
      try {
        holder.setShowsPlaybackControls(config.showControls)
      } catch (_: Throwable) {
        onErrorState.value.invoke()
      }
    }
  )
}

private fun String.isHttpUrl(): Boolean =
  startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)

private fun String.isFileUrl(): Boolean = startsWith("file://", ignoreCase = true)

private fun toNsUrlOrNull(source: VideoSource): NSURL? {
  return when (source) {
    is VideoSource.Path -> NSURL.fileURLWithPath(source.path)
    is VideoSource.Url -> {
      val raw = source.url
      when {
        raw.isHttpUrl() || raw.isFileUrl() -> NSURL.URLWithString(raw)
        raw.startsWith("/") -> NSURL.fileURLWithPath(raw)
        else -> NSURL.URLWithString(raw)
      }
    }
  }
}

private class IosPlayerHolder(
  private val url: NSURL,
) {
  private var controller: AVPlayerViewController? = null
  private var view: UIView? = null

  val player: AVPlayer = AVPlayer(AVPlayerItem(uRL = url))

  private fun ensureController(): AVPlayerViewController {
    val existing = controller
    if (existing != null) return existing

    return AVPlayerViewController().apply {
      this.player = this@IosPlayerHolder.player
      showsPlaybackControls = true
      videoGravity = AVLayerVideoGravityResizeAspect
      view.backgroundColor = UIColor.blackColor
    }.also { created ->
      controller = created
      view = created.view
    }
  }

  fun ensureView(): UIView = view ?: ensureController().view

  fun setShowsPlaybackControls(show: Boolean) {
    ensureController().showsPlaybackControls = show
  }

  fun play() {
    player.play()
  }

  fun pause() {
    player.pause()
  }

  fun restartAndPlay() {
    // Recreate the item to restart playback. Avoids relying on seek APIs that may differ.
    player.replaceCurrentItemWithPlayerItem(AVPlayerItem(uRL = url))
    player.play()
  }

  fun dispose() {
    player.pause()
    player.replaceCurrentItemWithPlayerItem(null)
    controller?.player = null
    view = null
    controller = null
  }
}
