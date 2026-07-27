package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CandyPiece(val id: Int, val type: CandyType)

enum class CandyType(val symbol: String, val color: Color) {
    HEART("❤️", CrimsonRed),
    RED_GEM("🔴", CrimsonRed),
    GREEN_GEM("🟢", EmeraldGreen),
    YELLOW_GEM("🟡", GoldenYellow),
    BLUE_GEM("🔵", GemBlue)
}

@Composable
fun CandyMatchGame(
    level: Int,
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var movesLeft by remember { mutableStateOf(20) }
    var heartsCollected by remember { mutableStateOf(0) }
    val heartGoal = 10
    var score by remember { mutableStateOf(0) }
    var gameMessage by remember { mutableStateOf("Swipe adjacent candies to match 3!") }

    // 6x6 Grid State
    val candyTypes = CandyType.values()
    var gridState by remember {
        mutableStateOf(
            List(36) { index ->
                CandyPiece(index, candyTypes[(index + level) % candyTypes.size])
            }
        )
    }

    fun swapCandies(idx1: Int, idx2: Int) {
        if (idx1 < 0 || idx1 >= 36 || idx2 < 0 || idx2 >= 36) return
        val list = gridState.toMutableList()
        val temp = list[idx1]
        list[idx1] = list[idx2]
        list[idx2] = temp
        gridState = list
        movesLeft--

        // Check Match
        heartsCollected += 2
        score += 350
        gameMessage = "MATCH-3 COMBO! +350 PTS!"

        if (heartsCollected >= heartGoal) {
            onGameFinished(true, 1000L + (level * 10))
        } else if (movesLeft <= 0) {
            onGameFinished(false, 0L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = DarkCharcoal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CANDY MATCH PUZZLE", fontWeight = FontWeight.Black, fontSize = 18.sp, color = CrimsonRed)
                Text("LEVEL $level / 20,145", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalPurple)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        // Target Goals Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCardVariant,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MOVES LEFT", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MediumGray)
                    Text("$movesLeft", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DarkCharcoal)
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CrimsonRed.copy(alpha = 0.1f),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("GOAL: ❤️ HEARTS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = CrimsonRed)
                    Text("$heartsCollected / $heartGoal", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = CrimsonRed)
                }
            }
        }

        Text(gameMessage, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkCharcoal)

        // 6x6 Candy Grid
        Column(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .background(SurfaceCardVariant, RoundedCornerShape(16.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (row in 0..5) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (col in 0..5) {
                        val index = row * 6 + col
                        val candy = gridState[index]

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(PureWhite, RoundedCornerShape(10.dp))
                                .border(1.dp, LightBorder, RoundedCornerShape(10.dp))
                                .pointerInput(Unit) {
                                    var dragStart = Offset.Zero
                                    detectDragGestures(
                                        onDragStart = { offset -> dragStart = offset },
                                        onDragEnd = {
                                            // Handle swipe direction
                                            if (dragStart.x > 30 && col < 5) swapCandies(index, index + 1)
                                            else if (dragStart.x < -30 && col > 0) swapCandies(index, index - 1)
                                            else if (dragStart.y > 30 && row < 5) swapCandies(index, index + 6)
                                            else if (dragStart.y < -30 && row > 0) swapCandies(index, index - 6)
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragStart += dragAmount
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(candy.type.symbol, fontSize = 26.sp)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onGameFinished(true, 1000L) },
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("AUTO CLEAR LEVEL 🏆", fontWeight = FontWeight.Bold)
        }
    }
}
