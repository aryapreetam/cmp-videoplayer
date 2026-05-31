package io.github.aryapreetam.cmpvideoplayer

/** Input for [VideoPlayer]. Only video files are in scope for this library. */
public sealed interface VideoSource {
  /** A raw platform file path. */
  public data class Path(val path: String) : VideoSource

  /** A URL string (e.g., `https://`, `file://`, on Android also `content://`). */
  public data class Url(val url: String) : VideoSource
}
