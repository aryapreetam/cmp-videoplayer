package io.github.aryapreetam.cmpvideoplayer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

/**
 * UI test.
 *
 * Note: JVM/desktop Compose UI tests may require additional native/skiko runtime setup.
 * This test is excluded from default JVM unit test runs via Gradle (`*UITest*`).
 */
class VideoPlayerPlaceholderOnlyUITest {
  @OptIn(ExperimentalTestApi::class)
  @Test
  fun placeholderOnly_rendersWithoutInterop() = runComposeUiTest {
    setContent {
      VideoPlayer(
        source = VideoSource.Url("https://example.com/video.mp4"),
        config = VideoPlayerConfig(renderMode = RenderMode.PlaceholderOnly)
      )
    }

    onNodeWithTag(VideoPlayerTestTags.Root).assertIsDisplayed()
  }
}
