package com.example.games.chess

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

data class ChessPiece(val type: PieceType, val isWhite: Boolean)
enum class PieceType { PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessGameScreen(onBack: () -> Unit) {
  fun createInitialBoard(): Array<Array<ChessPiece?>> {
    val board = Array(8) { arrayOfNulls<ChessPiece>(8) }
    board[0] = arrayOf(
      ChessPiece(PieceType.ROOK, false),
      ChessPiece(PieceType.KNIGHT, false),
      ChessPiece(PieceType.BISHOP, false),
      ChessPiece(PieceType.QUEEN, false),
      ChessPiece(PieceType.KING, false),
      ChessPiece(PieceType.BISHOP, false),
      ChessPiece(PieceType.KNIGHT, false),
      ChessPiece(PieceType.ROOK, false)
    )
    for (c in 0 until 8) board[1][c] = ChessPiece(PieceType.PAWN, false)

    for (c in 0 until 8) board[6][c] = ChessPiece(PieceType.PAWN, true)
    board[7] = arrayOf(
      ChessPiece(PieceType.ROOK, true),
      ChessPiece(PieceType.KNIGHT, true),
      ChessPiece(PieceType.BISHOP, true),
      ChessPiece(PieceType.QUEEN, true),
      ChessPiece(PieceType.KING, true),
      ChessPiece(PieceType.BISHOP, true),
      ChessPiece(PieceType.KNIGHT, true),
      ChessPiece(PieceType.ROOK, true)
    )
    return board
  }

  var board by remember { mutableStateOf(createInitialBoard()) }
  var selectedSquare by remember { mutableStateOf<Pair<Int, Int>?>(null) }
  var isWhiteTurn by remember { mutableStateOf(true) }
  var message by remember { mutableStateOf("White's turn. Select a piece.") }

  fun pieceSymbol(piece: ChessPiece): String {
    return when (piece.type) {
      PieceType.PAWN -> if (piece.isWhite) "♙" else "♟"
      PieceType.ROOK -> if (piece.isWhite) "♖" else "♜"
      PieceType.KNIGHT -> if (piece.isWhite) "♘" else "♞"
      PieceType.BISHOP -> if (piece.isWhite) "♗" else "♝"
      PieceType.QUEEN -> if (piece.isWhite) "♕" else "♛"
      PieceType.KING -> if (piece.isWhite) "♔" else "♚"
    }
  }

  fun onSquareClick(r: Int, c: Int) {
    val piece = board[r][c]
    val selected = selectedSquare

    if (selected == null) {
      if (piece != null && piece.isWhite == isWhiteTurn) {
        selectedSquare = Pair(r, c)
        message = "Selected ${piece.type}. Tap destination."
      }
    } else {
      val (sr, sc) = selected
      if (sr == r && sc == c) {
        selectedSquare = null
        message = "Deselected."
      } else {
        val newBoard = board.map { it.clone() }.toTypedArray()
        newBoard[r][c] = newBoard[sr][sc]
        newBoard[sr][sc] = null
        board = newBoard
        selectedSquare = null
        isWhiteTurn = !isWhiteTurn
        message = if (isWhiteTurn) "White's turn." else "Black's turn."
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Grandmaster Chess") },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = {
            board = createInitialBoard()
            selectedSquare = null
            isWhiteTurn = true
            message = "White's turn. Select a piece."
          }) {
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
        Text(message, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
      }

      Card(
        modifier = Modifier.size(350.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          for (r in 0 until 8) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
              for (c in 0 until 8) {
                val isDark = (r + c) % 2 == 1
                val isSelected = selectedSquare?.let { it.first == r && it.second == c } ?: false
                val bgColor = when {
                  isSelected -> Color(0xFFFF007F)
                  isDark -> Color(0xFF2D3748)
                  else -> Color(0xFFE2E8F0)
                }
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(bgColor)
                    .clickable { onSquareClick(r, c) },
                  contentAlignment = Alignment.Center
                ) {
                  board[r][c]?.let { piece ->
                    Text(
                      text = pieceSymbol(piece),
                      fontSize = 28.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (piece.isWhite && !isDark) Color.Black else if (!piece.isWhite && isDark) Color.White else Color.DarkGray
                    )
                  }
                }
              }
            }
          }
        }
      }

      Text("Tap piece then destination square to play", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
