package com.example.games.pool

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class Ball(var pos: Offset, var vel: Offset, val color: Color, val id: Int, var active: Boolean = true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoolGameScreen(onBack: () -> Unit) {
  var cueBall by remember { mutableStateOf(Ball(Offset(200f, 400f), Offset.Zero, Color.White, 0)) }
  var targetBalls by remember {
    mutableStateOf(
      listOf(
        Ball(Offset(200f, 200f), Offset.Zero, Color(0xFFFFD700), 1),
        Ball(Offset(180f, 160f), Offset.Zero, Color(0xFFEF4444), 2),
        Ball(Offset(220f, 160f), Offset.Zero, Color(0xFF22C55E), 3),
        Ball(Offset(200f, 120f), Offset.Zero, Color(0xFF9900FF), 4)
      )
    )
  }

  var dragStart by remember { mutableStateOf<Offset?>(null) }
  var dragCurrent by remember { mutableStateOf<Offset?>(null) }
  var score by remember { mutableStateOf(0) }
  var isMoving by remember { mutableStateOf(false) }

  val tableWidth = 340f
  val tableHeight = 560f
  val pocketRadius = 24f

  val pockets = listOf(
    Offset(20f, 20f),
    Offset(tableWidth / 2, 15f),
    Offset(tableWidth - 20f, 20f),
    Offset(20f, tableHeight - 20f),
    Offset(tableWidth / 2, tableHeight - 15f),
    Offset(tableWidth - 20f, tableHeight - 20f)
  )

  fun resetTable() {
    cueBall = Ball(Offset(tableWidth / 2, tableHeight - 100f), Offset.Zero, Color.White, 0)
    targetBalls = listOf(
      Ball(Offset(tableWidth / 2, 200f), Offset.Zero, Color(0xFFFFD700), 1),
      Ball(Offset(tableWidth / 2 - 20f, 160f), Offset.Zero, Color(0xFFEF4444), 2),
      Ball(Offset(tableWidth / 2 + 20f, 160f), Offset.Zero, Color(0xFF22C55E), 3),
      Ball(Offset(tableWidth / 2, 120f), Offset.Zero, Color(0xFF9900FF), 4)
    )
    score = 0
    isMoving = false
  }

  LaunchedEffect(isMoving) {
    if (isMoving) {
      while (isMoving) {
        delay(16L)
        val friction = 0.985f
        var movingAny = false

        if (cueBall.active) {
          var newPos = cueBall.pos + cueBall.vel
          var newVel = cueBall.vel * friction
          if (newVel.getDistance() < 0.2f) newVel = Offset.Zero else movingAny = true

          val r = 12f
          if (newPos.x < r + 20f || newPos.x > tableWidth - r - 20f) newVel = Offset(-newVel.x, newVel.y)
          if (newPos.y < r + 20f || newPos.y > tableHeight - r - 20f) newVel = Offset(newVel.x, -newVel.y)

          pockets.forEach { pocket ->
            if ((newPos - pocket).getDistance() < pocketRadius) {
              newPos = Offset(tableWidth / 2, tableHeight - 100f)
              newVel = Offset.Zero
            }
          }
          cueBall = cueBall.copy(pos = newPos, vel = newVel)
        }

        val updatedTargets = targetBalls.map { ball ->
          if (!ball.active) return@map ball
          var newPos = ball.pos + ball.vel
          var newVel = ball.vel * friction
          if (newVel.getDistance() >= 0.2f) movingAny = true else newVel = Offset.Zero

          val r = 12f
          if (newPos.x < r + 20f || newPos.x > tableWidth - r - 20f) newVel = Offset(-newVel.x, newVel.y)
          if (newPos.y < r + 20f || newPos.y > tableHeight - r - 20f) newVel = Offset(newVel.x, -newVel.y)

          var active = true
          pockets.forEach { pocket ->
            if ((newPos - pocket).getDistance() < pocketRadius) {
              active = false
              score += 100
            }
          }
          ball.copy(pos = newPos, vel = newVel, active = active)
        }

        targetBalls = updatedTargets.filter { it.active }
        isMoving = movingAny
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Neon 8-Ball Pool") },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { resetTable() }) {
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
        Text("Score: $score | Balls Remaining: ${targetBalls.size}", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleMedium)
      }

      Card(
        modifier = Modifier
          .width(340.dp)
          .height(560.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D5C32))
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .pointerInput(isMoving) {
              if (!isMoving) {
                detectDragGestures(
                  onDragStart = { offset ->
                    dragStart = offset
                    dragCurrent = offset
                  },
                  onDrag = { change, _ ->
                    dragCurrent = change.position
                  },
                  onDragEnd = {
                    if (dragStart != null && dragCurrent != null) {
                      val pull = dragStart!! - dragCurrent!!
                      val power = Offset(pull.x * 0.15f, pull.y * 0.15f)
                      cueBall = cueBall.copy(vel = power)
                      isMoving = true
                    }
                    dragStart = null
                    dragCurrent = null
                  }
                )
              }
            }
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            pockets.forEach { pocket ->
              drawCircle(color = Color.Black, radius = pocketRadius, center = pocket)
            }

            if (cueBall.active) {
              drawCircle(color = cueBall.color, radius = 12f, center = cueBall.pos)
            }

            targetBalls.forEach { ball ->
              if (ball.active) {
                drawCircle(color = ball.color, radius = 12f, center = ball.pos)
              }
            }

            if (dragStart != null && dragCurrent != null) {
              drawLine(
                color = Color.Yellow,
                start = cueBall.pos,
                end = cueBall.pos + (dragStart!! - dragCurrent!!),
                strokeWidth = 3f
              )
            }
          }
        }
      }

      Text("Drag back from cue ball to aim and shoot", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
