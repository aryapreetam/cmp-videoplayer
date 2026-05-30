package io.github.aryapreetam.cmpvideoplayer

import kotlin.test.Test
import kotlin.test.assertEquals

class VideoPlayerConfigTest {
  @Test
  fun defaults_areExpected() {
    val config = VideoPlayerConfig()
    assertEquals(true, config.showControls)
    assertEquals(false, config.autoplay)
    assertEquals(false, config.loop)
    assertEquals(true, config.exclusivePlayback)
    assertEquals(LifecyclePolicy.PlatformDefault, config.lifecyclePolicy)
    assertEquals(RenderMode.Platform, config.renderMode)
  }
}
