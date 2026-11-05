package com.example.model

data class GameItem(
  val id: String,
  val title: String,
  val category: String,
  val description: String,
  val iconName: String,
  val players: String,
  val difficulty: String,
  val rating: Float = 4.8f
)

object ArcadeGames {
  val list = listOf(
    GameItem(
      id = "snake",
      title = "Cyber Snake",
      category = "Action",
      description = "Guide the neon snake to eat glowing food points without crashing.",
      iconName = "extension",
      players = "1 Player",
      difficulty = "Medium"
    ),
    GameItem(
      id = "pool",
      title = "Neon 8-Ball Pool",
      category = "Sports",
      description = "Aim, set power, and sink the billiard balls into pockets.",
      iconName = "sports_bar",
      players = "1-2 Players",
      difficulty = "Hard"
    ),
    GameItem(
      id = "ludo",
      title = "Royal Ludo",
      category = "Board",
      description = "Roll the dice, race your 4 tokens to home, and block rivals.",
      iconName = "casino",
      players = "2-4 Players",
      difficulty = "Medium"
    ),
    GameItem(
      id = "chess",
      title = "Grandmaster Chess",
      category = "Strategy",
      description = "Classic chess engine with AI opponent and legal move highlights.",
      iconName = "psychology",
      players = "1-2 Players",
      difficulty = "Expert"
    ),
    GameItem(
      id = "tictactoe",
      title = "Tic-Tac-Toe Pro",
      category = "Board",
      description = "Classic 3x3 tactical grid battle against smart AI or a friend.",
      iconName = "grid_view",
      players = "1-2 Players",
      difficulty = "Easy"
    ),
    GameItem(
      id = "memory",
      title = "Memory Matrix",
      category = "Puzzle",
      description = "Flip cards and match glowing neon pairs in record time.",
      iconName = "style",
      players = "1 Player",
      difficulty = "Medium"
    ),
    GameItem(
      id = "brickbreaker",
      title = "Brick Breaker",
      category = "Action",
      description = "Bounce the energy ball off your paddle to smash all neon bricks.",
      iconName = "view_module",
      players = "1 Player",
      difficulty = "Medium"
    ),
    GameItem(
      id = "minesweeper",
      title = "Neon Minesweeper",
      category = "Puzzle",
      description = "Uncover grid tiles while avoiding hidden cyber-mines.",
      iconName = "bolt",
      players = "1 Player",
      difficulty = "Hard"
    )
  )
}
