package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.games.brickbreaker.BrickBreakerScreen
import com.example.games.chess.ChessGameScreen
import com.example.games.ludo.LudoGameScreen
import com.example.games.memory.MemoryMatchScreen
import com.example.games.minesweeper.MinesweeperScreen
import com.example.games.pool.PoolGameScreen
import com.example.games.snake.SnakeGameScreen
import com.example.games.tictactoe.TicTacToeScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.leaderboard.LeaderboardScreen
import com.example.ui.profile.UserProfileScreen
import com.example.ui.theme.ClassicArcadeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      var isDarkTheme by remember { mutableStateOf(true) }
      var currentScreen by remember { mutableStateOf("dashboard") }

      ClassicArcadeTheme(darkTheme = isDarkTheme) {
        if (currentScreen != "dashboard") {
          BackHandler {
            currentScreen = "dashboard"
          }
        }

        when (currentScreen) {
          "dashboard" -> DashboardScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = { isDarkTheme = !isDarkTheme },
            onSelectGame = { gameId -> currentScreen = gameId },
            onOpenProfile = { currentScreen = "profile" },
            onOpenLeaderboard = { currentScreen = "leaderboard" }
          )
          "leaderboard" -> LeaderboardScreen(
            onBack = { currentScreen = "dashboard" },
            currentUserId = "local_user_1",
            currentUserName = "Arcade Player"
          )
          "profile" -> UserProfileScreen(
            onBack = { currentScreen = "dashboard" },
            onOpenLeaderboard = { currentScreen = "leaderboard" }
          )
          "snake" -> SnakeGameScreen(
            onBack = { currentScreen = "dashboard" },
            onOpenLeaderboard = { currentScreen = "leaderboard" }
          )
          "pool" -> PoolGameScreen(onBack = { currentScreen = "dashboard" })
          "ludo" -> LudoGameScreen(
            onBack = { currentScreen = "dashboard" },
            currentUserId = "local_user_1",
            currentUserName = "Arcade Player"
          )
          "chess" -> ChessGameScreen(onBack = { currentScreen = "dashboard" })
          "tictactoe" -> TicTacToeScreen(onBack = { currentScreen = "dashboard" })
          "memory" -> MemoryMatchScreen(onBack = { currentScreen = "dashboard" })
          "brickbreaker" -> BrickBreakerScreen(
            onBack = { currentScreen = "dashboard" },
            onOpenLeaderboard = { currentScreen = "leaderboard" }
          )
          "minesweeper" -> MinesweeperScreen(onBack = { currentScreen = "dashboard" })
          else -> DashboardScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = { isDarkTheme = !isDarkTheme },
            onSelectGame = { gameId -> currentScreen = gameId },
            onOpenProfile = { currentScreen = "profile" },
            onOpenLeaderboard = { currentScreen = "leaderboard" }
          )
        }
      }
    }
  }
}
