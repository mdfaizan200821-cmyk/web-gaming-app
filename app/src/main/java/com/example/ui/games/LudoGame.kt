package com.example.ui.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun LudoGame(
    stake: StakeOption,
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var diceValue by remember { mutableStateOf(6) }
    var isRolling by remember { mutableStateOf(false) }
    var redPawnProgress by remember { mutableStateOf(12) }
    var gameMessage by remember { mutableStateOf("Roll dice to move Red Pawns!") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = DarkCharcoal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LUDO RACE BOARD", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DarkCharcoal)
                Text("STAKE: 🪙 ${stake.entryFee}", fontSize = 12.sp, color = GoldenAmber)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        // Ludo Board Graphic Grid
        Surface(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = PureWhite,
            border = BorderStroke(3.dp, DarkCharcoal)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Half: Green & Yellow Home Bases
                Row(modifier = Modifier.weight(2f)) {
                    // Green Quadrant
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .background(EmeraldGreen)
                            .padding(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = PureWhite,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🟩 GREEN", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    // Vertical Path
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SurfaceCardVariant)
                            .border(1.dp, LightBorder)
                    )
                    // Yellow Quadrant
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .background(GoldenYellow)
                            .padding(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = PureWhite,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🟨 YELLOW", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Center Cross & Home Triangle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(RoyalPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👑 HOME TRIANGLE", fontWeight = FontWeight.Black, color = RoyalPurple)
                }

                // Bottom Half: Red & Blue Home Bases
                Row(modifier = Modifier.weight(2f)) {
                    // Red Quadrant (Player)
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .background(CrimsonRed)
                            .padding(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = PureWhite,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("🟥 YOUR RED BASE", fontWeight = FontWeight.Bold, color = CrimsonRed, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("♟️", fontSize = 20.sp)
                                    Text("♟️", fontSize = 20.sp)
                                    Text("♟️", fontSize = 20.sp)
                                    Text("♟️", fontSize = 20.sp)
                                }
                            }
                        }
                    }
                    // Vertical Path
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SurfaceCardVariant)
                            .border(1.dp, LightBorder)
                    )
                    // Blue Quadrant
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .background(GemBlue)
                            .padding(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = PureWhite,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🟦 BLUE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Text(gameMessage, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkCharcoal)

        // Dice Roll Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .clickable {
                        diceValue = (1..6).random()
                        redPawnProgress += diceValue
                        gameMessage = "Rolled $diceValue! Red Pawn moved to step $redPawnProgress/57"
                    },
                shape = RoundedCornerShape(16.dp),
                color = GoldenYellow,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (diceValue) {
                            1 -> "⚀"
                            2 -> "⚁"
                            3 -> "⚂"
                            4 -> "⚃"
                            5 -> "⚄"
                            else -> "⚅"
                        },
                        fontSize = 42.sp,
                        color = PureWhite
                    )
                }
            }

            Button(
                onClick = { onGameFinished(true, stake.prizePool) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("REACHED HOME! 🏆", fontWeight = FontWeight.Black)
            }
        }
    }
}
