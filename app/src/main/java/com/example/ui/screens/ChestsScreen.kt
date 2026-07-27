package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.models.ChestOption
import com.example.ui.models.PRESET_CHESTS
import com.example.ui.theme.*

@Composable
fun ChestsScreen(
    userGems: Int,
    onOpenChestClick: (ChestOption) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CrispBackground)
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "MYSTERY CHESTS & POWER BOXES",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = DarkCharcoal
                )
                Text(
                    text = "Unlock rare avatars, golden frames, and massive coin drops!",
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }
        }

        items(PRESET_CHESTS) { chest ->
            val canAfford = userGems >= chest.gemPrice

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PureWhite,
                border = BorderStroke(1.5.dp, chest.color.copy(alpha = 0.4f)),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📦", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = chest.name,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = DarkCharcoal
                                )
                                Box(
                                    modifier = Modifier
                                        .background(chest.color, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = chest.badge,
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💎 ", fontSize = 16.sp)
                            Text(
                                text = "${chest.gemPrice}",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = DarkCharcoal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceCardVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🪙 Coins Reward: ${String.format("%,d", chest.minCoinsReward)} - ${String.format("%,d", chest.maxCoinsReward)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "💎 Guaranteed Bonus: +${chest.guaranteedGems} Gems",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = GemCyan
                            )
                            Text(
                                text = "👑 Includes rare avatars & Divine Golden Aura Frames!",
                                fontSize = 11.sp,
                                color = MediumGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onOpenChestClick(chest) },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = chest.color,
                            disabledContainerColor = LightBorder
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (canAfford) "OPEN BOX (${chest.gemPrice} 💎)" else "INSUFFICIENT GEMS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = if (canAfford) PureWhite else MediumGray
                        )
                    }
                }
            }
        }
    }
}
