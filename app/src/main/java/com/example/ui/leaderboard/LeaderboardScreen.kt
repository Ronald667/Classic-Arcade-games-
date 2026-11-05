package com.example.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LeaderboardRepository
import com.example.model.LeaderboardEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
  onBack: () -> Unit,
  currentUserId: String = "local_user_1",
  currentUserName: String = "Arcade Player"
) {
  val context = LocalContext.current
  val repo = remember { LeaderboardRepository(context) }
  val scoresState = repo.observeTopScores(limitCount = 20).collectAsState(initial = emptyList())
  val rawScores = scoresState.value

  var selectedGameFilter by remember { mutableStateOf("All") }
  var showSubmitDialog by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  val filteredScores = remember(rawScores, selectedGameFilter) {
    val list = if (selectedGameFilter == "All") {
      rawScores
    } else {
      rawScores.filter { it.gameTitle.equals(selectedGameFilter, ignoreCase = true) }
    }
    list.sortedByDescending { it.score }.take(10)
  }

  val gameCategories = listOf("All", "Cyber Snake", "Brick Breaker", "Neon 8-Ball Pool", "Royal Ludo")

  Scaffold(
    modifier = Modifier.testTag("leaderboard_screen"),
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = Color(0xFFFFD700)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Global Leaderboard", fontWeight = FontWeight.Bold)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { showSubmitDialog = true },
            modifier = Modifier.testTag("submit_score_action_button")
          ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Submit Score", tint = MaterialTheme.colorScheme.primary)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    Color(0xFF00FFCC).copy(alpha = 0.2f),
                    Color(0xFFFF007F).copy(alpha = 0.2f)
                  )
                )
              )
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Badge(containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)) {
                  Text(
                    "⭐ TOP 10 LEGENDS",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  "Arcade Champions",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  "Top highest-scoring players across all arcade games.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Button(
                onClick = { showSubmitDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Post Score", style = MaterialTheme.typography.labelMedium)
              }
            }
          }
        }
      }

      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(gameCategories) { category ->
            FilterChip(
              selected = selectedGameFilter == category,
              onClick = { selectedGameFilter = category },
              label = { Text(category) }
            )
          }
        }
      }

      if (filteredScores.isNotEmpty()) {
        item {
          PodiumSection(topScores = filteredScores.take(3), currentUserId = currentUserId)
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "Rankings (Top ${filteredScores.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (filteredScores.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.height(8.dp))
              Text("No scores yet for this filter", fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(12.dp))
              Button(onClick = { showSubmitDialog = true }) {
                Text("Submit Score Now")
              }
            }
          }
        }
      } else {
        itemsIndexed(filteredScores) { index, entry ->
          val rank = index + 1
          LeaderboardRow(rank = rank, entry = entry, isCurrentUser = entry.userId == currentUserId)
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  if (showSubmitDialog) {
    SubmitScoreDialog(
      currentUserName = currentUserName,
      onDismiss = { showSubmitDialog = false },
      onSubmit = { gameId, gameTitle, scoreValue ->
        repo.submitScore(
          userId = currentUserId,
          userName = currentUserName,
          gameId = gameId,
          gameTitle = gameTitle,
          score = scoreValue,
          onSuccess = {
            showSubmitDialog = false
            statusMessage = "Score posted to Leaderboard!"
          },
          onError = { err ->
            statusMessage = "Error: $err"
          }
        )
      }
    )
  }
}

@Composable
fun PodiumSection(topScores: List<LeaderboardEntry>, currentUserId: String) {
  Card(
    modifier = Modifier.fillMaxWidth().testTag("podium_first_place"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        "Hall of Fame (Top 3)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
      ) {
        if (topScores.size >= 2) {
          PodiumColumn(
            modifier = Modifier.weight(1f),
            entry = topScores[1],
            rank = 2,
            height = 130.dp,
            badgeColor = Color(0xFFC0C0C0),
            isCurrentUser = topScores[1].userId == currentUserId
          )
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }

        if (topScores.isNotEmpty()) {
          PodiumColumn(
            modifier = Modifier.weight(1.1f),
            entry = topScores[0],
            rank = 1,
            height = 155.dp,
            badgeColor = Color(0xFFFFD700),
            isCurrentUser = topScores[0].userId == currentUserId
          )
        }

        if (topScores.size >= 3) {
          PodiumColumn(
            modifier = Modifier.weight(1f),
            entry = topScores[2],
            rank = 3,
            height = 115.dp,
            badgeColor = Color(0xFFCD7F32),
            isCurrentUser = topScores[2].userId == currentUserId
          )
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
fun PodiumColumn(
  modifier: Modifier = Modifier,
  entry: LeaderboardEntry,
  rank: Int,
  height: androidx.compose.ui.unit.Dp,
  badgeColor: Color,
  isCurrentUser: Boolean
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(badgeColor.copy(alpha = 0.25f)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = when (rank) {
          1 -> "👑"
          2 -> "🥈"
          else -> "🥉"
        },
        fontSize = 18.sp
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = entry.userName,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      text = "${entry.score}",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.ExtraBold,
      color = badgeColor
    )
    Spacer(modifier = Modifier.height(4.dp))
    Surface(
      modifier = Modifier.fillMaxWidth().height(height),
      shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
      color = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          "#$rank",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Black,
          color = badgeColor
        )
        Text(
          entry.gameTitle,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
fun LeaderboardRow(
  rank: Int,
  entry: LeaderboardEntry,
  isCurrentUser: Boolean
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("leaderboard_item_${entry.id}")
      .then(
        if (isCurrentUser) {
          Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
        } else Modifier
      ),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant
      }
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(
            when (rank) {
              1 -> Color(0xFFFFD700).copy(alpha = 0.2f)
              2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
              3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
              else -> MaterialTheme.colorScheme.surface
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "#$rank",
          fontWeight = FontWeight.Bold,
          color = when (rank) {
            1 -> Color(0xFFFFD700)
            2 -> Color(0xFFC0C0C0)
            3 -> Color(0xFFCD7F32)
            else -> MaterialTheme.colorScheme.onSurface
          },
          fontSize = 13.sp
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = entry.userName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (isCurrentUser) {
            Spacer(modifier = Modifier.width(6.dp))
            Badge(containerColor = MaterialTheme.colorScheme.primary) {
              Text("YOU", color = MaterialTheme.colorScheme.onPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
        Text(
          text = entry.gameTitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${entry.score}",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Black,
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = "PTS",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun SubmitScoreDialog(
  currentUserName: String,
  onDismiss: () -> Unit,
  onSubmit: (gameId: String, gameTitle: String, score: Long) -> Unit
) {
  val gameOptions = listOf(
    Pair("snake", "Cyber Snake"),
    Pair("brickbreaker", "Brick Breaker"),
    Pair("pool", "Neon 8-Ball Pool"),
    Pair("ludo", "Royal Ludo"),
    Pair("chess", "Grandmaster Chess")
  )

  var selectedGame by remember { mutableStateOf(gameOptions[0]) }
  var scoreText by remember { mutableStateOf("150") }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Submit Arcade Score", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Record your high score to the leaderboard as $currentUserName:")

        Text("Select Game:", style = MaterialTheme.typography.labelMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          gameOptions.take(3).forEach { option ->
            FilterChip(
              selected = selectedGame.first == option.first,
              onClick = { selectedGame = option },
              label = { Text(option.second.take(8)) }
            )
          }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          gameOptions.drop(3).forEach { option ->
            FilterChip(
              selected = selectedGame.first == option.first,
              onClick = { selectedGame = option },
              label = { Text(option.second.take(12)) }
            )
          }
        }

        OutlinedTextField(
          value = scoreText,
          onValueChange = {
            if (it.all { char -> char.isDigit() } && it.length <= 6) {
              scoreText = it
              errorText = null
            }
          },
          label = { Text("Score Points") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("score_input_field")
        )

        if (errorText != null) {
          Text(errorText!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val parsed = scoreText.toLongOrNull() ?: 0L
          if (parsed <= 0) {
            errorText = "Please enter a score greater than 0"
          } else {
            onSubmit(selectedGame.first, selectedGame.second, parsed)
          }
        },
        modifier = Modifier.testTag("submit_score_dialog_button")
      ) {
        Text("Submit")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
