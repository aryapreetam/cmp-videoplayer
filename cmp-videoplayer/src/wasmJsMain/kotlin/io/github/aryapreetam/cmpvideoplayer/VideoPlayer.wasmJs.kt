@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.aryapreetam.cmpvideoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.WebElementView
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.events.Event
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsArray
import kotlin.js.JsNumber
import kotlin.js.js
import kotlin.js.toJsNumber

internal actual fun isVideoSourceSupported(source: VideoSource): Boolean = source is VideoSource.Url

private object ActiveHtmlVideoRegistry {
  private var active: HTMLVideoElement? = null

  fun setActive(video: HTMLVideoElement) {
    val previous = active
    if (previous != null && previous !== video) {
      previous.pause()
    }
    active = video
  }

  fun clear(video: HTMLVideoElement) {
    if (active === video) {
      active = null
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
  val onErrorState = rememberUpdatedState(onError)
  val url = (source as? VideoSource.Url)?.url

  if (url == null) {
    LaunchedEffect(source) { onErrorState.value.invoke() }
    return
  }

  val container = remember(url) {
    createVideoContainer(url)
  }

  DisposableEffect(url, config.showControls, config.autoplay, config.loop, config.exclusivePlayback) {
    val video = container.querySelector("video") as? HTMLVideoElement
    if (video == null) {
      onErrorState.value.invoke()
      return@DisposableEffect onDispose { }
    }

    video.controls = config.showControls
    video.autoplay = config.autoplay
    video.loop = config.loop

    try {
      if (config.autoplay) {
        video.play()
      } else {
        video.pause()
      }
    } catch (_: Throwable) {
      // Best-effort.
    }

    val playHandler: (Event) -> Unit = {
      if (config.exclusivePlayback) {
        ActiveHtmlVideoRegistry.setActive(video)
      }
    }

    video.addEventListener("play", playHandler)

    onDispose {
      try {
        video.pause()
        video.src = ""
      } catch (_: Throwable) {
        // Best-effort.
      }
      if (config.exclusivePlayback) {
        ActiveHtmlVideoRegistry.clear(video)
      }
      video.removeEventListener("play", playHandler)
    }
  }

  WebElementView(
    factory = { container },
    modifier = modifier,
    update = { /* No-op */ }
  )
}

private fun createVideoContainer(url: String): HTMLDivElement {
  val container = document.createElement("div") as HTMLDivElement
  container.style.width = "100%"
  container.style.height = "100%"
  container.style.display = "flex"
  container.style.alignItems = "center"
  container.style.justifyContent = "center"
  container.style.background = "black"

  val video = document.createElement("video") as HTMLVideoElement
  video.src = url
  video.controls = true
  video.autoplay = false
  video.preload = "none"
  video.style.maxWidth = "100%"
  video.style.maxHeight = "100%"
  video.style.setProperty("object-fit", "contain")
  container.appendChild(video)

  return container
}

/**
 * Creates and remembers a `blob:` URL for a video stored as bytes (e.g., `Res.raw.*` on wasm).
 *
 * The returned URL is revoked on disposal.
 */
@Composable
public fun rememberBlobVideoUrl(
  bytes: ByteArray,
  mimeType: String,
): String {
  val url = remember(bytes, mimeType) {
    createBlobObjectUrl(bytes, mimeType)
  }

  DisposableEffect(url) {
    onDispose {
      try {
        revokeBlobObjectUrl(url)
      } catch (_: Throwable) {
        // Best-effort.
      }
    }
  }

  return url
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun createBlobObjectUrl(bytes: ByteArray, mimeType: String): String {
  // Convert ByteArray -> JS array of numbers -> Uint8Array -> Blob -> object URL.
  // (We do not pass ByteArray to JS interop directly, as it is not supported on wasm.)
  val numbers = JsArray<JsNumber>()
  for (i in bytes.indices) {
    numbers[i] = (bytes[i].toInt() and 0xFF).toJsNumber()
  }

  val u8: JsAny = newUint8Array(numbers)
  val parts = JsArray<JsAny?>()
  parts[0] = u8

  return createObjectUrl(parts, mimeType)
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun revokeBlobObjectUrl(url: String) {
  js("URL.revokeObjectURL(url)")
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun newUint8Array(numbers: JsArray<JsNumber>): JsAny =
  js("new Uint8Array(numbers)")

@OptIn(ExperimentalWasmJsInterop::class)
private fun createObjectUrl(parts: JsArray<JsAny?>, mimeType: String): String =
  js("URL.createObjectURL(new Blob(parts, { type: mimeType }))")
