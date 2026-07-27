package com.example.ui.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.models.StakeOption
import com.example.ui.theme.*

data class CardItem(val suit: String, val rank: String, val color: Color, var isSelected: Boolean = false)

@Composable
fun RummyGame(
    stake: StakeOption,
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var playerCards by remember {
        mutableStateOf(
            listOf(
                CardItem("♠", "A", DarkCharcoal), CardItem("♠", "2", DarkCharcoal), CardItem("♠", "3", DarkCharcoal),
                CardItem("♥️", "10", CrimsonRed), CardItem("♥️", "J", CrimsonRed), CardItem("♥️", "Q", CrimsonRed),
                CardItem("♦️", "7", CrimsonRed), CardItem("♦️", "8", CrimsonRed), CardItem("♦️", "9", CrimsonRed),
                CardItem("♣️", "4", DarkCharcoal), CardItem("♣️", "5", DarkCharcoal), CardItem("♣️", "6", DarkCharcoal),
                CardItem("♠", "K", DarkCharcoal)
            )
        )
    }

    var isPlayerTurn by remember { mutableStateOf(true) }
    var gameMessage by remember { mutableStateOf("Arrange 13 cards into valid sets & sequences") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F522E)) // Felt green rummy table
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Table Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = PureWhite)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("13-CARD RUMMY TABLE", color = GoldenYellow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("STAKE: 🪙 ${stake.entryFee}", color = PureWhite, fontSize = 12.sp)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        // Opponent Bot Slot
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PureWhite.copy(alpha = 0.15f),
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🤖 OPPONENT BOT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("🂠 13 Cards Hidden", color = GoldenYellow, fontSize = 11.sp)
            }
        }

        // Table Center: Stock Pile & Discard Pile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Closed Stock Deck
            CardBox("🂠", "STOCK", PureWhite, RoyalPurple) {
                if (isPlayerTurn) {
                    gameMessage = "Card Drawn from Stock! Select a card to discard."
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Open Discard Deck
            CardBox("♥️ Q", "DISCARD", CrimsonRed, SurfaceCard) {
                if (isPlayerTurn) {
                    gameMessage = "Picked Heart Q from Discard Deck!"
                }
            }
        }

        Text(gameMessage, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        // Player Cards Hand
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("YOUR 13 CARDS (TAP TO SELECT)", color = GoldenYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy((-16).dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(playerCards) { index, card ->
                    RummyCard(card = card) {
                        playerCards = playerCards.mapIndexed { i, c ->
                            if (i == index) c.copy(isSelected = !c.isSelected) else c
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: SORT | GROUP | DECLARE WIN
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        playerCards = playerCards.sortedBy { it.rank }
                        gameMessage = "Cards sorted by Rank!"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SORT", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onGameFinished(true, stake.prizePool)
                    },
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("DECLARE WIN 🏆", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun CardBox(text: String, label: String, textColor: Color, bgColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(width = 64.dp, height = 90.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            color = bgColor,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = PureWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RummyCard(card: CardItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .offset(y = if (card.isSelected) (-16).dp else 0.dp)
            .size(width = 54.dp, height = 80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = PureWhite,
        border = BorderStroke(1.5.dp, if (card.isSelected) GoldenYellow else LightBorder),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(card.rank, fontWeight = FontWeight.Black, fontSize = 14.sp, color = card.color)
            Text(card.suit, fontSize = 18.sp, color = card.color)
        }
    }
}
