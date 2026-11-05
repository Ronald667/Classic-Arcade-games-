package com.example.games.ludo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Token(val id: Int, val player: Int, var position: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LudoGameScreen(
  onBack: () -> Unit,
  currentUserId: String = "local_user_1",
  currentUserName: String = "Arcade Player"
) {
  var isOnlineMode by remember { mutableStateOf(false) }
  var currentPlayer by remember { mutableStateOf(0) }
  var diceValue by remember { mutableStateOf(6) }
  var hasRolled by remember { mutableStateOf(false) }
  var message by remember { mutableStateOf("Red's turn. Roll the dice!") }
  val coroutineScope = rememberCoroutineScope()

  val playerColors = listOf(Color(0xFFEF4444), Color(0xFF3B82F6))
  val playerNames = listOf(currentUserName, "AI Challenger")

  var tokens by remember {
    mutableStateOf(
      List(2) { p -> List(2) { t -> Token(t, p, -1) } }.flatten()
    )
  }

  fun resetGame() {
    currentPlayer = 0
    hasRolled = false
    diceValue = 6
    tokens = List(2) { p -> List(2) { t -> Token(t, p, -1) } }.flatten()
    message = "${playerNames[currentPlayer]}'s turn. Roll the dice!"
  }

  fun rollDice() {
    if (!hasRolled && (!isOnlineMode || currentPlayer == 0)) {
      diceValue = Random.nextInt(1, 7)
      hasRolled = true
      message = "Rolled $diceValue! Tap your token to move."
    }
  }

  fun moveToken(token: Token) {
    if (!hasRolled) {
      message = "Please roll the dice first!"
      return
    }
    if (token.player != currentPlayer) {
      message = "Not your turn!"
      return
    }

    val updatedTokens = tokens.map { tk ->
      if (tk.player == token.player && tk.id == token.id) {
        if (tk.position == -1) {
          if (diceValue == 6) tk.copy(position = 0) else tk
        } else {
          val newPos = minOf(40, tk.position + diceValue)
          tk.copy(position = newPos)
        }
      } else tk
    }

    tokens = updatedTokens
    hasRolled = false
    currentPlayer = (currentPlayer + 1) % 2

    if (isOnlineMode && currentPlayer == 1) {
      message = "AI Challenger's turn..."
      coroutineScope.launch {
        delay(1200L)
        val botRoll = Random.nextInt(1, 7)
        diceValue = botRoll
        val botTokens = tokens.filter { it.player == 1 }
        val tokenToMove = botTokens.firstOrNull { it.position >= 0 } ?: botTokens.first()
        tokens = tokens.map { tk ->
          if (tk.player == 1 && tk.id == tokenToMove.id) {
            val p = if (tk.position == -1) { if (botRoll == 6) 0 else -1 } else minOf(40, tk.position + botRoll)
            tk.copy(position = p)
          } else tk
        }
        currentPlayer = 0
        hasRolled = false
        message = "AI Challenger rolled $botRoll and moved. Your turn!"
      }
    } else {
      message = "${playerNames[currentPlayer]}'s turn. Roll the dice!"
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(if (isOnlineMode) "Royal Ludo (VS AI Bot)" else "Royal Ludo (Pass & Play)") },
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
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Choose Ludo Game Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(12.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { isOnlineMode = false; resetGame() },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = if (!isOnlineMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            ) {
              Text("Pass & Play")
            }
            Button(
              onClick = { isOnlineMode = true; resetGame() },
              modifier = Modifier.weight(1f).testTag("online_match_button"),
              colors = ButtonDefaults.buttonColors(containerColor = if (isOnlineMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            ) {
              Text("VS AI Bot")
            }
          }
        }
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Text(message, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
      }

      Card(
        modifier = Modifier.size(340.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(
          modifier = Modifier.fillMaxSize().padding(16.dp),
          verticalArrangement = Arrangement.SpaceAround
        ) {
          playerColors.forEachIndexed { pIdx, color ->
            Card(
              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(playerNames[pIdx], color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                  tokens.filter { it.player == pIdx }.forEach { tk ->
                    Box(
                      modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (currentPlayer == pIdx) color else color.copy(alpha = 0.4f))
                        .clickable { moveToken(tk) },
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = if (tk.position == -1) "Base" else "Pos ${tk.position}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = playerColors[currentPlayer],
          modifier = Modifier.size(72.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text("$diceValue", style = MaterialTheme.typography.headlineLarge, color = Color.White)
          }
        }

        Button(
          onClick = { rollDice() },
          enabled = !hasRolled && (!isOnlineMode || currentPlayer == 0),
          modifier = Modifier.height(56.dp).testTag("roll_dice_button")
        ) {
          Text("Roll Dice", style = MaterialTheme.typography.titleMedium)
        }
      }
    }
  }
}
