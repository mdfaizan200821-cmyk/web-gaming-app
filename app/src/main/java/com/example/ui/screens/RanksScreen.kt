package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val avatarSymbol: String,
    val coinsWon: Long,
    val winRate: String,
    val isUser: Boolean = false
)

val MOCK_LEADERBOARD = listOf(
    LeaderboardEntry(1, "Apex Legend", "👑", 2850000L, "88%"),
    LeaderboardEntry(2, "Master Gamer (You)", "M", 10815L, "78%", isUser = true),
    LeaderboardEntry(3, "Viper Queen", "♕", 1420000L, "81%"),
    LeaderboardEntry(4, "Cyber Samurai", "⚔️", 980000L, "75%"),
    LeaderboardEntry(5, "Gold Dragon", "🐉", 820000L, "72%"),
    LeaderboardEntry(6, "Shadow King", "♔", 650000L, "70%"),
    LeaderboardEntry(7, "Neon Knight", "🛡️", 510000L, "68%")
)

@Composable
fun RanksScreen() {
    var selectedTimeframe by remember { mutableStateOf(0) } // 0: Weekly, 1: Daily, 2: All-Time

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CrispBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "HALL OF FAME & RANKS",
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = DarkCharcoal
        )
        Text(
            text = "Compete weekly for golden rewards and glory",
            fontSize = 12.sp,
            color = MediumGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Timeframe Selector Tabs
        TabRow(
            selectedTabIndex = selectedTimeframe,
            containerColor = SurfaceCardVariant,
            contentColor = RoyalPurple,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTimeframe == 0,
                onClick = { selectedTimeframe = 0 },
                text = { Text("WEEKLY", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTimeframe == 1,
                onClick = { selectedTimeframe = 1 },
                text = { Text("DAILY", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTimeframe == 2,
                onClick = { selectedTimeframe = 2 },
                text = { Text("ALL-TIME", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Podium Top 3 View
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // 2nd Place
            PodiumCard(
                rank = "2nd",
                entry = MOCK_LEADERBOARD[2],
                color = Color(0xFFC0C0C0),
                modifier = Modifier.weight(1f),
                height = 110.dp
            )
            // 1st Place
            PodiumCard(
                rank = "1st",
                entry = MOCK_LEADERBOARD[0],
                color = GoldenYellow,
                modifier = Modifier.weight(1.1f),
                height = 135.dp
            )
            // 3rd Place
            PodiumCard(
                rank = "3rd",
                entry = MOCK_LEADERBOARD[3],
                color = Color(0xFFCD7F32),
                modifier = Modifier.weight(1f),
                height = 95.dp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Full Rankings List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(MOCK_LEADERBOARD) { index, entry ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (entry.isUser) ElectricIndigo.copy(alpha = 0.1f) else SurfaceCardVariant,
                    border = BorderStroke(
                        1.dp,
                        if (entry.isUser) RoyalPurple else LightBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        when (entry.rank) {
                                            1 -> GoldenYellow
                                            2 -> Color(0xFFC0C0C0)
                                            3 -> Color(0xFFCD7F32)
                                            else -> MediumGray
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${entry.rank}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PureWhite
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = entry.avatarSymbol,
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = entry.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DarkCharcoal
                                )
                                Text(
                                    text = "Win Rate: ${entry.winRate}",
                                    fontSize = 11.sp,
                                    color = MediumGray
                                )
                            }
                        }

                        Text(
                            text = "🪙 ${String.format("%,d", entry.coinsWon)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = DarkCharcoal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumCard(rank: String, entry: LeaderboardEntry, color: Color, modifier: Modifier, height: androidx.compose.ui.unit.Dp) {
    Surface(
        modifier = modifier.height(height),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.5.dp, color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(entry.avatarSymbol, fontSize = 22.sp)
            Text(entry.name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarkCharcoal)
            Box(
                modifier = Modifier
                    .background(color, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(rank, fontWeight = FontWeight.Black, fontSize = 10.sp, color = PureWhite)
            }
        }
    }
}
