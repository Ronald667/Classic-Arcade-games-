package com.example.games.tictactoe

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(onBack: () -> Unit) {
  var board by remember { mutableStateOf(List(9) { "" }) }
  var isXTurn by remember { mutableStateOf(true) }
  var winner by remember { mutableStateOf<String?>(null) }

  fun checkWinner(b: List<String>): String? {
    val lines = listOf(
      listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
      listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
      listOf(0, 4, 8), listOf(2, 4, 6)
    )
    for (l in lines) {
      if (b[l[0]].isNotEmpty() && b[l[0]] == b[l[1]] && b[l[0]] == b[l[2]]) {
        return b[l[0]]
      }
    }
    if (b.all { it.isNotEmpty() }) return "Draw"
    return null
  }

  fun onCellClick(index: Int) {
    if (board[index].isEmpty() && winner == null) {
      val newBoard = board.toMutableList()
      newBoard[index] = if (isXTurn) "X" else "O"
      board = newBoard
      winner = checkWinner(newBoard)
      isXTurn = !isXTurn
    }
  }

  fun resetGame() {
    board = List(9) { "" }
    isXTurn = true
    winner = null
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Tic-Tac-Toe Pro") },
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
        val status = when (winner) {
          "X" -> "Winner: X"
          "O" -> "Winner: O"
          "Draw" -> "It's a Draw!"
          else -> "Turn: ${if (isXTurn) "X" else "O"}"
        }
        Text(status, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
      }

      Card(
        modifier = Modifier.size(320.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(
          modifier = Modifier.fillMaxSize().padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          for (row in 0 until 3) {
            Row(
              modifier = Modifier.weight(1f).fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              for (col in 0 until 3) {
                val index = row * 3 + col
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .clickable { onCellClick(index) },
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = board[index],
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (board[index] == "X") Color(0xFF00FFCC) else Color(0xFFFF007F)
                  )
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
