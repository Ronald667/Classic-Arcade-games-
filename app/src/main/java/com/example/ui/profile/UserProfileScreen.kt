package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
  onBack: () -> Unit,
  onOpenLeaderboard: () -> Unit = {}
) {
  var displayName by remember { mutableStateOf("Arcade Player") }
  var isEditing by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  fun saveDisplayName() {
    statusMessage = "Display name updated successfully!"
    isEditing = false
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Player Profile") },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      Box(
        modifier = Modifier
          .size(96.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          modifier = Modifier.size(56.dp),
          tint = MaterialTheme.colorScheme.primary
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isEditing) {
          OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display Name") },
            singleLine = true,
            modifier = Modifier.testTag("display_name_input")
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { saveDisplayName() }, modifier = Modifier.testTag("save_profile_button")) {
              Text("Save")
            }
            OutlinedButton(onClick = { isEditing = false }) {
              Text("Cancel")
            }
          }
        } else {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = displayName,
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { isEditing = true }, modifier = Modifier.testTag("edit_profile_button")) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Name")
            }
          }
          Text(
            text = "arcade.player@local.device",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      if (statusMessage != null) {
        Text(statusMessage!!, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

      // Stats Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Arcade Statistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatItem(label = "Total Wins", value = "18")
            StatItem(label = "Games Played", value = "42")
            StatItem(label = "Win Rate", value = "42.8%")
          }
        }
      }

      // Leaderboard Quick-Link Card
      Card(
        onClick = onOpenLeaderboard,
        modifier = Modifier.fillMaxWidth().testTag("profile_leaderboard_button"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700))
            Column {
              Text("Global Leaderboard", fontWeight = FontWeight.Bold)
              Text("View top 10 rankings across all games", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
      }

      // Favorite Game Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Favorite Game", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Casino, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
              Text("Royal Ludo", fontWeight = FontWeight.Bold)
              Text("Most played & Featured Game of the Week", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }
    }
  }
}

@Composable
fun StatItem(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}
