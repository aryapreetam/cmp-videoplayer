package sample.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPlayerDemo() {
  val defaultUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

  var input by remember { mutableStateOf(defaultUrl) }
  var currentSource by remember { mutableStateOf<VideoSource>(VideoSource.Url(defaultUrl)) }

  var showControls by remember { mutableStateOf(true) }
  var loop by remember { mutableStateOf(false) }

  // We keep explicit user intent to start playback (better UX than autoplaying on load).
  var isPlaying by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "cmp-videoplayer sample",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("https://…/video.mp4  or  /path/to/video.mp4") }
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = {
                currentSource = toSource(input)
                isPlaying = true
              }
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
              }
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
            Switch(checked = showControls, onCheckedChange = { showControls = it })
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Loop")
            Switch(checked = loop, onCheckedChange = { loop = it })
          }
        }
      }

      Text(text = "Player", style = MaterialTheme.typography.titleMedium)

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Color.Black),
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
                modifier = Modifier.size(56.dp)
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

      Text(
        text = "Current source: ${describeSource(currentSource)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
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