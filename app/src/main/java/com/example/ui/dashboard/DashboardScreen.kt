package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArcadeGames
import com.example.model.GameItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  isDarkTheme: Boolean,
  onToggleTheme: () -> Unit,
  onSelectGame: (String) -> Unit,
  onOpenProfile: () -> Unit,
  onOpenLeaderboard: () -> Unit
) {
  var selectedCategory by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }

  val categories = listOf("All", "Action", "Sports", "Board", "Strategy", "Puzzle")

  // Core requested games: Snake, Pool, Ludo, Chess
  val coreGameIds = listOf("snake", "pool", "ludo", "chess")
  val coreGames = ArcadeGames.list.filter { it.id in coreGameIds }

  val filteredGames = ArcadeGames.list.filter { game ->
    val matchesCategory = selectedCategory == "All" || game.category == selectedCategory
    val matchesSearch = game.title.contains(searchQuery, ignoreCase = true) ||
      game.description.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesSearch
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Classic Arcade Hub", fontWeight = FontWeight.Bold)
          }
        },
        actions = {
          IconButton(
            onClick = onOpenLeaderboard,
            modifier = Modifier.testTag("leaderboard_button")
          ) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = "Global Leaderboard",
              tint = Color(0xFFFFD700)
            )
          }
          IconButton(
            onClick = onOpenProfile,
            modifier = Modifier.testTag("profile_button")
          ) {
            Icon(Icons.Default.Person, contentDescription = "Profile")
          }
          IconButton(
            onClick = onToggleTheme,
            modifier = Modifier.testTag("theme_toggle_button")
          ) {
            Icon(
              imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.WbSunny,
              contentDescription = "Toggle Theme"
            )
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
        // Featured Game banner
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectGame("ludo") }
            .testTag("featured_game_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  colors = if (isDarkTheme) {
                    listOf(Color(0xFFFF007F).copy(alpha = 0.8f), Color(0xFF9900FF).copy(alpha = 0.8f))
                  } else {
                    listOf(Color(0xFFD97706).copy(alpha = 0.9f), Color(0xFFDC2626).copy(alpha = 0.9f))
                  }
                )
              )
              .padding(20.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Badge(containerColor = Color.White.copy(alpha = 0.2f)) {
                  Text("🔥 FEATURED GAME OF THE WEEK", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Royal Ludo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Real-time multiplayer & turn sync! Roll the dice and race your tokens now.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                  onClick = { onSelectGame("ludo") },
                  colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                  Text("Play Now", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      // Core Games Grid Section (Snake, Pool, Ludo, Chess)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Featured Classics (Snake, Pool, Ludo, Chess)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          // 2x2 Grid of Core Games
          val coreChunks = coreGames.chunked(2)
          coreChunks.forEach { rowGames ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              rowGames.forEach { game ->
                Box(modifier = Modifier.weight(1f)) {
                  GameGridCard(game = game, onClick = { onSelectGame(game.id) })
                }
              }
              if (rowGames.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
          }
        }
      }

      // Global Leaderboard Banner Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenLeaderboard)
            .testTag("dashboard_leaderboard_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                "Global Leaderboard",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                "Top 10 highest-scoring players across all arcade games.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            FilledTonalButton(
              onClick = onOpenLeaderboard,
              modifier = Modifier.testTag("view_leaderboard_button")
            ) {
              Text("View")
            }
          }
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isDarkTheme) "Theme: Classic Neon (Dark)" else "Theme: Vintage Cabinet (Light)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          TextButton(onClick = onToggleTheme) {
            Text(if (isDarkTheme) "Switch to Vintage Cabinet" else "Switch to Classic Neon")
          }
        }
      }

      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search classic games...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("search_input"),
          singleLine = true
        )
      }

      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(categories) { category ->
            FilterChip(
              selected = selectedCategory == category,
              onClick = { selectedCategory = category },
              label = { Text(category) }
            )
          }
        }
      }

      item {
        Text("Complete Arcade Library (${filteredGames.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      }

      items(filteredGames) { game ->
        GameCard(game = game, onClick = { onSelectGame(game.id) })
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun GameGridCard(game: GameItem, onClick: () -> Unit) {
  Card(
    modifier = Modifier
      .height(140.dp)
      .clickable(onClick = onClick)
      .testTag("game_grid_card_${game.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when (game.iconName) {
              "sports_bar" -> Icons.Default.SportsBar
              "casino" -> Icons.Default.Casino
              "psychology" -> Icons.Default.Psychology
              "grid_view" -> Icons.Default.GridView
              "style" -> Icons.Default.Style
              "view_module" -> Icons.Default.ViewModule
              "bolt" -> Icons.Default.Bolt
              else -> Icons.Default.Extension
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }
        Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
          Text(game.category, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
        }
      }
      Column {
        Text(
          text = game.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = game.players,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun GameCard(game: GameItem, onClick: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("game_card_${game.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (game.iconName) {
            "sports_bar" -> Icons.Default.SportsBar
            "casino" -> Icons.Default.Casino
            "psychology" -> Icons.Default.Psychology
            "grid_view" -> Icons.Default.GridView
            "style" -> Icons.Default.Style
            "view_module" -> Icons.Default.ViewModule
            "bolt" -> Icons.Default.Bolt
            else -> Icons.Default.Extension
          },
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(28.dp)
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(game.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
            Text(game.category, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(game.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("👥 ${game.players}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text("⚡ ${game.difficulty}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text("⭐ ${game.rating}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
  }
}
