package com.example.games.memory

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CardItem(val id: Int, val symbol: String, var isFaceUp: Boolean = false, var isMatched: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMatchScreen(onBack: () -> Unit) {
  val symbols = listOf("★", "▲", "■", "●", "♦", "♠", "♣", "♥")
  fun createCards(): List<CardItem> {
    val selected = symbols.shuffled().take(8)
    val doubled = (selected + selected).shuffled()
    return doubled.mapIndexed { index, sym -> CardItem(index, sym) }
  }

  var cards by remember { mutableStateOf(createCards()) }
  var firstSelection by remember { mutableStateOf<Int?>(null) }
  var isChecking by remember { mutableStateOf(false) }
  var matches by remember { mutableStateOf(0) }
  val coroutineScope = rememberCoroutineScope()

  fun resetGame() {
    cards = createCards()
    firstSelection = null
    isChecking = false
    matches = 0
  }

  fun onCardClick(index: Int) {
    if (isChecking || cards[index].isFaceUp || cards[index].isMatched) return

    val newCards = cards.toMutableList()
    newCards[index] = newCards[index].copy(isFaceUp = true)
    cards = newCards

    val first = firstSelection
    if (first == null) {
      firstSelection = index
    } else {
      isChecking = true
      coroutineScope.launch {
        delay(600L)
        if (cards[first].symbol == cards[index].symbol) {
          val matchedCards = cards.toMutableList()
          matchedCards[first] = matchedCards[first].copy(isMatched = true)
          matchedCards[index] = matchedCards[index].copy(isMatched = true)
          cards = matchedCards
          matches++
        } else {
          val resetCards = cards.toMutableList()
          resetCards[first] = resetCards[first].copy(isFaceUp = false)
          resetCards[index] = resetCards[index].copy(isFaceUp = false)
          cards = resetCards
        }
        firstSelection = null
        isChecking = false
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Memory Matrix") },
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
        Text("Matches: $matches / 8", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
      }

      Column(
        modifier = Modifier.size(340.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        for (row in 0 until 4) {
          Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            for (col in 0 until 4) {
              val index = row * 4 + col
              val card = cards[index]
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight()
                  .background(
                    if (card.isFaceUp || card.isMatched) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(8.dp)
                  )
                  .clickable { onCardClick(index) },
                contentAlignment = Alignment.Center
              ) {
                if (card.isFaceUp || card.isMatched) {
                  Text(text = card.symbol, fontSize = 32.sp, color = MaterialTheme.colorScheme.onPrimary)
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
