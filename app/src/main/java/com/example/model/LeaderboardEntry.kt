package com.example.model

data class LeaderboardEntry(
  val id: String = "",
  val userId: String = "",
  val userName: String = "",
  val gameId: String = "all",
  val gameTitle: String = "Classic Arcade",
  val score: Long = 0L,
  val timestamp: Long = System.currentTimeMillis()
)
