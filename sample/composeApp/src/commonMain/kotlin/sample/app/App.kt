package sample.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.aryapreetam.cmpvideoplayer.VideoPlayer
import io.github.aryapreetam.cmpvideoplayer.VideoPlayerConfig
import io.github.aryapreetam.cmpvideoplayer.VideoSource

@Composable
fun App() {
  MaterialTheme {
    VideoPlayerDemo()
  }
}

@Composable
private fun VideoPlayerDemo() {
  val defaultUrl = "https://samplelib.com/mp4/sample-5s-360p.mp4"

  var input by remember { mutableStateOf(defaultUrl) }
  var currentSource by remember { mutableStateOf<VideoSource>(VideoSource.Url(defaultUrl)) }

  var showControls by remember { mutableStateOf(true) }
  var loop by remember { mutableStateOf(false) }

  // We keep explicit user intent to start playback (better UX than autoplaying on load).
  var isPlaying by remember { mutableStateOf(false) }

  Scaffold { padding ->
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .testTag("main_container")
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      val controlsContent = @Composable { modifier: Modifier ->
        Column(
          modifier = modifier,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "cmp-videoplayer sample",
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("sample_title")
          )

          Text(
            text = "Plays video files across Android/iOS/JVM/wasm.",
            style = MaterialTheme.typography.bodyMedium
          )
          Text(
            text = "Desktop/JVM requires VLC installed (mediamp/VLC backend).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(text = "Video URL or file path", style = MaterialTheme.typography.titleMedium)

              OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("video_url_input"),
                placeholder = { Text("https://…/video.mp4  or  /path/to/video.mp4") }
              )

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = {
                    currentSource = toSource(input)
                    isPlaying = true
                  },
                  modifier = Modifier.testTag("load_and_play_button")
                ) {
                  Icon(Icons.Default.PlayArrow, contentDescription = null)
                  Spacer(Modifier.width(8.dp))
                  Text("Load & Play")
                }

                Button(
                  onClick = {
                    input = defaultUrl
                    currentSource = VideoSource.Url(defaultUrl)
                    isPlaying = false
                  },
                  modifier = Modifier.testTag("reset_button")
                ) {
                  Text("Reset")
                }
              }

              HorizontalDivider()

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Show controls")
                Switch(
                  checked = showControls,
                  onCheckedChange = { showControls = it },
                  modifier = Modifier.testTag("show_controls_switch")
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Loop")
                Switch(
                  checked = loop,
                  onCheckedChange = { loop = it },
                  modifier = Modifier.testTag("loop_switch")
                )
              }
            }
          }
        }
      }

      val playerContent = @Composable { modifier: Modifier ->
        Column(
          modifier = modifier,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Player", style = MaterialTheme.typography.titleMedium)
            Text(
              text = if (isPlaying) "Status: Playing" else "Status: Idle",
              style = MaterialTheme.typography.bodySmall,
              color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.testTag("playback_status_text")
            )
          }

          Text(
            text = "Current source: ${describeSource(currentSource)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("current_source_text")
          )

          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.Black)
                .testTag("player_container"),
              contentAlignment = Alignment.Center
            ) {
              if (isPlaying) {
                VideoPlayer(
                  source = currentSource,
                  config = VideoPlayerConfig(
                    autoplay = true,
                    showControls = showControls,
                    loop = loop,
                  ),
                  modifier = Modifier.fillMaxSize(),
                  placeholder = {
                    Text(
                      text = "Unable to load video",
                      color = Color.White,
                      style = MaterialTheme.typography.bodyMedium
                    )
                  }
                )
              } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "Press play to start",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                  )
                  Spacer(Modifier.height(8.dp))
                  IconButton(
                    onClick = {
                      currentSource = toSource(input)
                      isPlaying = true
                    },
                    modifier = Modifier.size(56.dp).testTag("idle_play_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.PlayArrow,
                      contentDescription = "Play",
                      tint = Color.White
                    )
                  }
                }
              }
            }
          }
        }
      }

      if (maxWidth >= 700.dp) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
          controlsContent(Modifier.weight(1f))
          playerContent(Modifier.weight(1f))
        }
      } else {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          controlsContent(Modifier.fillMaxWidth())
          playerContent(Modifier.fillMaxWidth())
        }
      }
    }
  }
}

private fun toSource(text: String): VideoSource {
  val trimmed = text.trim()
  val lower = trimmed.lowercase()
  return when {
    lower.startsWith("http://") ||
      lower.startsWith("https://") ||
      lower.startsWith("file://") ||
      lower.startsWith("content://") ||
      lower.startsWith("android.resource://") ||
      lower.startsWith("blob:") -> VideoSource.Url(trimmed)

    trimmed.startsWith("/") -> VideoSource.Path(trimmed)

    else -> VideoSource.Url(trimmed)
  }
}

private fun describeSource(source: VideoSource): String = when (source) {
  is VideoSource.Path -> "Path(${source.path})"
  is VideoSource.Url -> "Url(${source.url})"
}