package com.example.games.brickbreaker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.LeaderboardRepository
import kotlinx.coroutines.delay

data class Brick(var pos: Offset, val size: Size, var active: Boolean = true, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrickBreakerScreen(
  onBack: () -> Unit,
  onOpenLeaderboard: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val repo = remember { LeaderboardRepository(context) }

  var ballPos by remember { mutableStateOf(Offset(170f, 400f)) }
  var ballVel by remember { mutableStateOf(Offset(3f, -4f)) }
  var paddleX by remember { mutableStateOf(140f) }
  var score by remember { mutableStateOf(0) }
  var isPlaying by remember { mutableStateOf(true) }
  var scoreSubmitted by remember { mutableStateOf(false) }

  val tableWidth = 340f
  val tableHeight = 520f

  fun createBricks(): List<Brick> {
    val list = mutableListOf<Brick>()
    val cols = 6
    val rows = 4
    val brickWidth = 50f
    val brickHeight = 20f
    val colors = listOf(Color(0xFFFF007F), Color(0xFF00FFCC), Color(0xFF9900FF), Color(0xFFFFD700))

    for (r in 0 until rows) {
      for (c in 0 until cols) {
        list.add(
          Brick(
            pos = Offset(c * 54f + 14f, r * 24f + 40f),
            size = Size(brickWidth, brickHeight),
            color = colors[r % colors.size]
          )
        )
      }
    }
    return list
  }

  var bricks by remember { mutableStateOf(createBricks()) }

  fun resetGame() {
    ballPos = Offset(170f, 400f)
    ballVel = Offset(3f, -4f)
    paddleX = 140f
    bricks = createBricks()
    score = 0
    isPlaying = true
    scoreSubmitted = false
  }

  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      while (isPlaying) {
        delay(16L)
        var newPos = ballPos + ballVel
        var newVel = ballVel

        if (newPos.x < 10f || newPos.x > tableWidth - 10f) newVel = Offset(-newVel.x, newVel.y)
        if (newPos.y < 10f) newVel = Offset(newVel.x, -newVel.y)

        if (newPos.y >= tableHeight - 35f && newPos.y <= tableHeight - 20f) {
          if (newPos.x in paddleX..(paddleX + 80f)) {
            newVel = Offset(newVel.x, -abs(newVel.y))
          }
        }

        if (newPos.y > tableHeight) {
          isPlaying = false
        }

        val updatedBricks = bricks.map { brick ->
          if (brick.active) {
            if (newPos.x in brick.pos.x..(brick.pos.x + brick.size.width) &&
              newPos.y in brick.pos.y..(brick.pos.y + brick.size.height)
            ) {
              newVel = Offset(newVel.x, -newVel.y)
              score += 20
              return@map brick.copy(active = false)
            }
          }
          brick
        }

        bricks = updatedBricks
        ballPos = newPos
        ballVel = newVel
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Brick Breaker") },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          if (onOpenLeaderboard != null) {
            IconButton(onClick = onOpenLeaderboard) {
              Icon(Icons.Default.EmojiEvents, contentDescription = "Leaderboard", tint = Color(0xFFFFD700))
            }
          }
          IconButton(onClick = { resetGame() }) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Text("Score: $score", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleMedium)
      }

      Card(
        modifier = Modifier
          .width(340.dp)
          .height(520.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
              detectDragGestures { _, dragAmount ->
                paddleX = (paddleX + dragAmount.x).coerceIn(0f, tableWidth - 80f)
              }
            }
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            bricks.forEach { brick ->
              if (brick.active) {
                drawRect(color = brick.color, topLeft = brick.pos, size = brick.size)
              }
            }

            drawRect(
              color = Color.White,
              topLeft = Offset(paddleX, tableHeight - 30f),
              size = Size(80f, 14f)
            )

            drawCircle(color = Color(0xFF00FFCC), radius = 8f, center = ballPos)
          }

          if (!isPlaying) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f)),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
              ) {
                Text("GAME OVER", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFFF007F), fontWeight = FontWeight.Bold)
                Text("Final Score: $score", style = MaterialTheme.typography.titleMedium, color = Color.White)

                if (score > 0) {
                  if (scoreSubmitted) {
                    Text("✓ Score Submitted to Leaderboard!", color = Color(0xFF00FFCC), style = MaterialTheme.typography.bodySmall)
                  } else {
                    OutlinedButton(
                      onClick = {
                        repo.submitScore(
                          userId = "local_user_1",
                          userName = "Arcade Player",
                          gameId = "brickbreaker",
                          gameTitle = "Brick Breaker",
                          score = score.toLong(),
                          onSuccess = { scoreSubmitted = true }
                        )
                      },
                      modifier = Modifier.testTag("submit_brick_score_button")
                    ) {
                      Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Submit to Leaderboard")
                    }
                  }
                }

                Button(onClick = { resetGame() }, modifier = Modifier.testTag("restart_button")) {
                  Text("Play Again")
                }
              }
            }
          }
        }
      }

      Text("Drag horizontally to move paddle", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
fun abs(v: Float): Float = if (v < 0) -v else v
