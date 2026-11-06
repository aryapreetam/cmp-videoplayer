package sample.app

import io.github.aryapreetam.cmpvideoplayer.VideoPlayerTestTags
import io.github.aryapreetam.parikshan.E2ETestLifecycle
import io.github.aryapreetam.parikshan.e2eTest
import kotlin.test.Test
import kotlinx.coroutines.delay


class SampleIntegrationTest : E2ETestLifecycle {

  @Test
  fun testVideoPlayerWorkflow() = e2eTest {

    // 2. Tap Load & Play button
    click("load_and_play_button")

    // 3. Verify VideoPlayer root is mounted and playback state transitioned to Playing
    assertVisible(VideoPlayerTestTags.Root)
    assertContains("playback_status_text", "Status: Playing")
    assertContains("sample_title", "cmp-videoplayer sample")
    assertContains("current_source_text", "sample-5s-360p.mp4")
  }
}

