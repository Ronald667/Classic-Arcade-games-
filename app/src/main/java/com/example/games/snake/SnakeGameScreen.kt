package com.example.games.snake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.LeaderboardRepository
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }
data class Point(val x: Int, val y: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnakeGameScreen(
  onBack: () -> Unit,
  onOpenLeaderboard: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val repo = remember { LeaderboardRepository(context) }

  val gridSize = 20
  var snake by remember {
    mutableStateOf(listOf(Point(10, 10), Point(10, 11), Point(10, 12)))
  }
  var direction by remember { mutableStateOf(Direction.UP) }
  var food by remember { mutableStateOf(Point(5, 5)) }
  var score by remember { mutableStateOf(0) }
  var highScore by remember { mutableStateOf(0) }
  var isPlaying by remember { mutableStateOf(true) }
  var isGameOver by remember { mutableStateOf(false) }
  var scoreSubmitted by remember { mutableStateOf(false) }

  fun spawnFood(currentSnake: List<Point>): Point {
    var newFood: Point
    do {
      newFood = Point(Random.nextInt(gridSize), Random.nextInt(gridSize))
    } while (currentSnake.contains(newFood))
    return newFood
  }

  fun resetGame() {
    snake = listOf(Point(10, 10), Point(10, 11), Point(10, 12))
    direction = Direction.UP
    food = spawnFood(snake)
    score = 0
    isGameOver = false
    isPlaying = true
    scoreSubmitted = false
  }

  LaunchedEffect(isPlaying, isGameOver) {
    if (isPlaying && !isGameOver) {
      while (true) {
        delay(140L)
        val head = snake.first()
        val fixedHead = Point(
          (head.x + when (direction) {
            Direction.LEFT -> -1
            Direction.RIGHT -> 1
            else -> 0
          } + gridSize) % gridSize,
          (head.y + when (direction) {
            Direction.UP -> -1
            Direction.DOWN -> 1
            else -> 0
          } + gridSize) % gridSize
        )

        if (snake.drop(1).contains(fixedHead)) {
          isGameOver = true
          isPlaying = false
          if (score > highScore) highScore = score
          break
        }

        val ateFood = fixedHead == food
        val newSnake = mutableListOf(fixedHead).apply {
          addAll(snake)
          if (!ateFood) removeAt(size - 1)
        }

        if (ateFood) {
          score += 10
          food = spawnFood(newSnake)
        }
        snake = newSnake
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Cyber Snake") },
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
          IconButton(onClick = { isPlaying = !isPlaying }) {
            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Pause")
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
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text("Score: $score", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleMedium)
        }
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text("Best: $highScore", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleMedium)
        }
      }

      Card(
        modifier = Modifier
          .size(320.dp)
          .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = size.width / gridSize
            val cellHeight = size.height / gridSize

            drawRect(
              color = Color(0xFFFF007F),
              topLeft = Offset(food.x * cellWidth, food.y * cellHeight),
              size = Size(cellWidth - 2f, cellHeight - 2f)
            )

            snake.forEachIndexed { index, pt ->
              val color = if (index == 0) Color(0xFF00FFCC) else Color(0xFF22C55E)
              drawRect(
                color = color,
                topLeft = Offset(pt.x * cellWidth, pt.y * cellHeight),
                size = Size(cellWidth - 2f, cellHeight - 2f)
              )
            }
          }

          if (isGameOver) {
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
                          gameId = "snake",
                          gameTitle = "Cyber Snake",
                          score = score.toLong(),
                          onSuccess = { scoreSubmitted = true }
                        )
                      },
                      modifier = Modifier.testTag("submit_snake_score_button")
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

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 16.dp)
      ) {
        IconButton(
          onClick = { if (direction != Direction.DOWN) direction = Direction.UP },
          modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp))
        ) {
          Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = MaterialTheme.colorScheme.onPrimary)
        }
        Row(
          horizontalArrangement = Arrangement.spacedBy(48.dp),
          modifier = Modifier.padding(vertical = 8.dp)
        ) {
          IconButton(
            onClick = { if (direction != Direction.RIGHT) direction = Direction.LEFT },
            modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp))
          ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = MaterialTheme.colorScheme.onPrimary)
          }
          IconButton(
            onClick = { if (direction != Direction.LEFT) direction = Direction.RIGHT },
            modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp))
          ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = MaterialTheme.colorScheme.onPrimary)
          }
        }
        IconButton(
          onClick = { if (direction != Direction.UP) direction = Direction.DOWN },
          modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp))
        ) {
          Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = MaterialTheme.colorScheme.onPrimary)
        }
      }
    }
  }
}
