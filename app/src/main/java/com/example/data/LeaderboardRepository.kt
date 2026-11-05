package com.example.data

import android.content.Context
import com.example.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaderboardRepository(context: Context) {
  private val initialEntries = listOf(
    LeaderboardEntry(id = "1", userId = "cpu_1", userName = "PixelKing", gameId = "snake", gameTitle = "Cyber Snake", score = 980),
    LeaderboardEntry(id = "2", userId = "cpu_2", userName = "CyberViper", gameId = "brickbreaker", gameTitle = "Brick Breaker", score = 920),
    LeaderboardEntry(id = "3", userId = "cpu_3", userName = "RetroQueen", gameId = "pool", gameTitle = "Neon 8-Ball Pool", score = 800),
    LeaderboardEntry(id = "4", userId = "cpu_4", userName = "NeonNinja", gameId = "snake", gameTitle = "Cyber Snake", score = 740),
    LeaderboardEntry(id = "5", userId = "cpu_5", userName = "GlitchMaster", gameId = "brickbreaker", gameTitle = "Brick Breaker", score = 680),
    LeaderboardEntry(id = "6", userId = "cpu_6", userName = "ArcadeAce", gameId = "pool", gameTitle = "Neon 8-Ball Pool", score = 600),
    LeaderboardEntry(id = "7", userId = "cpu_7", userName = "NovaStrike", gameId = "snake", gameTitle = "Cyber Snake", score = 550),
    LeaderboardEntry(id = "8", userId = "cpu_8", userName = "QuantumByte", gameId = "brickbreaker", gameTitle = "Brick Breaker", score = 480),
    LeaderboardEntry(id = "9", userId = "cpu_9", userName = "VaporWave99", gameId = "ludo", gameTitle = "Royal Ludo", score = 450),
    LeaderboardEntry(id = "10", userId = "cpu_10", userName = "SynthRider", gameId = "pool", gameTitle = "Neon 8-Ball Pool", score = 400)
  )

  private val scoresFlow = MutableStateFlow(initialEntries)

  fun observeTopScores(limitCount: Long = 10): Flow<List<LeaderboardEntry>> {
    return scoresFlow.asStateFlow()
  }

  fun submitScore(
    userId: String,
    userName: String,
    gameId: String,
    gameTitle: String,
    score: Long,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    if (score <= 0) {
      onError("Score must be greater than 0")
      return
    }

    val newEntry = LeaderboardEntry(
      id = System.currentTimeMillis().toString(),
      userId = userId,
      userName = userName.ifBlank { "Arcade Player" },
      gameId = gameId,
      gameTitle = gameTitle,
      score = score,
      timestamp = System.currentTimeMillis()
    )

    val current = scoresFlow.value.toMutableList()
    current.add(newEntry)
    scoresFlow.value = current.sortedByDescending { it.score }
    onSuccess()
  }

  fun seedInitialScoresIfEmpty(onComplete: () -> Unit = {}) {
    onComplete()
  }
}
