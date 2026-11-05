package com.example.games.minesweeper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Cell(val r: Int, val c: Int, val isMine: Boolean, var isRevealed: Boolean = false, var neighborMines: Int = 0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinesweeperScreen(onBack: () -> Unit) {
  val size = 8
  val mineCount = 10

  fun createBoard(): List<List<Cell>> {
    var cells = mutableListOf<Cell>()
    for (r in 0 until size) {
      for (c in 0 until size) {
        cells.add(Cell(r, c, false))
      }
    }
    cells = cells.shuffled().toMutableList()
    for (i in 0 until mineCount) {
      cells[i] = cells[i].copy(isMine = true)
    }
    cells = cells.sortedWith(compareBy({ it.r }, { it.c })).toMutableList()

    val board = List(size) { r -> List(size) { c -> cells[r * size + c] } }

    for (r in 0 until size) {
      for (c in 0 until size) {
        if (!board[r][c].isMine) {
          var count = 0
          for (dr in -1..1) {
            for (dc in -1..1) {
              val nr = r + dr
              val nc = c + dc
              if (nr in 0 until size && nc in 0 until size && board[nr][nc].isMine) {
                count++
              }
            }
          }
          board[r][c].neighborMines = count
        }
      }
    }
    return board
  }

  var board by remember { mutableStateOf(createBoard()) }
  var isGameOver by remember { mutableStateOf(false) }
  var hasWon by remember { mutableStateOf(false) }

  fun resetGame() {
    board = createBoard()
    isGameOver = false
    hasWon = false
  }

  fun revealCell(row: Int, col: Int) {
    if (isGameOver || hasWon) return
    val cell = board[row][col]
    if (cell.isRevealed) return

    val newBoard = board.map { rList -> rList.map { it.copy() } }
    if (cell.isMine) {
      isGameOver = true
      newBoard.forEach { rList -> rList.forEach { if (it.isMine) it.isRevealed = true } }
    } else {
      newBoard[row][col].isRevealed = true
      val unrevealedSafe = newBoard.flatten().count { !it.isMine && !it.isRevealed }
      if (unrevealedSafe == 0) hasWon = true
    }
    board = newBoard
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Neon Minesweeper") },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
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
        val status = when {
          isGameOver -> "Game Over! You hit a mine."
          hasWon -> "Victory! All mines avoided."
          else -> "Clear the cyber grid without mines."
        }
        Text(status, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
      }

      Card(
        modifier = Modifier.size(340.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(
          modifier = Modifier.fillMaxSize().padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          for (r in 0 until size) {
            Row(
              modifier = Modifier.weight(1f).fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              for (c in 0 until size) {
                val cell = board[r][c]
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                      if (cell.isRevealed) Color(0xFF1D2840) else MaterialTheme.colorScheme.primary,
                      RoundedCornerShape(4.dp)
                    )
                    .clickable { revealCell(r, c) },
                  contentAlignment = Alignment.Center
                ) {
                  if (cell.isRevealed) {
                    if (cell.isMine) {
                      Text("💣", fontSize = 16.sp)
                    } else if (cell.neighborMines > 0) {
                      Text(
                        "${cell.neighborMines}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      Button(onClick = { resetGame() }) {
        Text("Restart Game")
      }
    }
  }
}
