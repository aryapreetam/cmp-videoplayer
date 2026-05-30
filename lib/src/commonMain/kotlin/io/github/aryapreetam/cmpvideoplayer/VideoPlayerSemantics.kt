package io.github.aryapreetam.cmpvideoplayer

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver

public object VideoPlayerSemantics {
  public val IsVideoPlayer: SemanticsPropertyKey<Boolean> =
    SemanticsPropertyKey("cmp-videoplayer:IsVideoPlayer")

  public val HasError: SemanticsPropertyKey<Boolean> =
    SemanticsPropertyKey("cmp-videoplayer:HasError")
}

public var SemanticsPropertyReceiver.isCmpVideoPlayer: Boolean by VideoPlayerSemantics.IsVideoPlayer
public var SemanticsPropertyReceiver.cmpVideoPlayerHasError: Boolean by VideoPlayerSemantics.HasError
