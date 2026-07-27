package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.UserEntity
import com.example.ui.models.MainGameType
import com.example.ui.models.FunZoneGameType
import com.example.ui.theme.*

@Composable
fun LobbyScreen(
    user: UserEntity?,
    onOpenMainGame: (MainGameType) -> Unit,
    onOpenFunZoneGame: (FunZoneGameType) -> Unit,
    onWatchAdClick: () -> Unit,
    onClaimDailyBonusClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CrispBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Daily Rewards & Ad Banners
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Watch Ad Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GemBlue,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(64.dp)
                        .clickable { onWatchAdClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF2563EB), Color(0xFF4F46E5))
                                )
                            )
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "WATCH AD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = PureWhite.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "+1,000 Coins",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = PureWhite
                            )
                        }
                        Text("📺", fontSize = 24.sp)
                    }
                }

                // Daily Bonus Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, LightBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clickable { onClaimDailyBonusClick() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "DAILY BONUS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MediumGray,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Claim Now 🎁",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = EmeraldGreen
                        )
                    }
                }
            }
        }

        // SECTION 1: MAIN STAKE GAMES (3D ULTRA GAMING CARDS)
        item {
            Column {
                Text(
                    text = "3D STAKE DUELS & TOURNAMENTS",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = MediumGray,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Feature 3D Banner Card: CARROM POOL
                    GamingHero3DCard(
                        title = "CARROM POOL 3D",
                        subtitle = "Pocket Queen & White Coins | Striker Aim Physics",
                        emoji = "🎯",
                        gradientColors = listOf(Color(0xFFB85D1B), Color(0xFFE5C188)),
                        badgeText = "POPULAR 🏆",
                        onClick = { onOpenMainGame(MainGameType.CARROM_POOL) }
                    )

                    // 2x2 Grid for Other Main Games
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MainGameCard3D(
                            gameType = MainGameType.CHESS,
                            emoji = "♟️",
                            gradient = listOf(Color(0xFF1E293B), Color(0xFF334155)),
                            subtitle = "Royal 8x8 Duel",
                            modifier = Modifier.weight(1f),
                            onClick = { onOpenMainGame(MainGameType.CHESS) }
                        )
                        MainGameCard3D(
                            gameType = MainGameType.RUMMY,
                            emoji = "🂠",
                            gradient = listOf(Color(0xFF831843), Color(0xFFBE185D)),
                            subtitle = "13-Card Melds",
                            modifier = Modifier.weight(1f),
                            onClick = { onOpenMainGame(MainGameType.RUMMY) }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MainGameCard3D(
                            gameType = MainGameType.LUDO,
                            emoji = "🎲",
                            gradient = listOf(Color(0xFF065F46), Color(0xFF059669)),
                            subtitle = "Dice Color Race",
                            modifier = Modifier.weight(1f),
                            onClick = { onOpenMainGame(MainGameType.LUDO) }
                        )
                        MainGameCard3D(
                            gameType = MainGameType.TIC_TAC_TOE,
                            emoji = "❌",
                            gradient = listOf(Color(0xFF9A3412), Color(0xFFEA580C)),
                            subtitle = "Fast 3x3 Match",
                            modifier = Modifier.weight(1f),
                            onClick = { onOpenMainGame(MainGameType.TIC_TAC_TOE) }
                        )
                    }
                }
            }
        }

        // SECTION 2: FUN ZONE - TRENDING POPULAR SOLO GAMES
        item {
            Column {
                Text(
                    text = "TRENDING FUN ZONE SOLO",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = MediumGray,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // SUBWAY RUNNER
                    FunZoneGameCard(
                        gameType = FunZoneGameType.SUBWAY_RUNNER,
                        emoji = "🏃",
                        description = "3D 3-Lane Endless Runner & Train Dodge",
                        badgeText = "3D RUNNER",
                        badgeColor = GemBlue,
                        onClick = { onOpenFunZoneGame(FunZoneGameType.SUBWAY_RUNNER) }
                    )

                    // FRUIT SLASH
                    FunZoneGameCard(
                        gameType = FunZoneGameType.FRUIT_SLASH,
                        emoji = "⚔️",
                        description = "Fast blade swiping & bomb dodge combos",
                        badgeText = "SLICING",
                        badgeColor = GoldenAmber,
                        onClick = { onOpenFunZoneGame(FunZoneGameType.FRUIT_SLASH) }
                    )

                    // BLOCK PUZZLE
                    FunZoneGameCard(
                        gameType = FunZoneGameType.BLOCK_PUZZLE,
                        emoji = "🧩",
                        description = "8x8 Grid block placement & line clear",
                        badgeText = "PUZZLE",
                        badgeColor = EmeraldGreen,
                        onClick = { onOpenFunZoneGame(FunZoneGameType.BLOCK_PUZZLE) }
                    )

                    // 3D TALKING CAT
                    FunZoneGameCard(
                        gameType = FunZoneGameType.TALKING_CAT,
                        emoji = "🐱",
                        description = "Voice pitch mimic & touch reactions",
                        badgeText = "3D PET",
                        badgeColor = RoyalPurple,
                        onClick = { onOpenFunZoneGame(FunZoneGameType.TALKING_CAT) }
                    )

                    // CANDY MATCH
                    FunZoneGameCard(
                        gameType = FunZoneGameType.CANDY_MATCH,
                        emoji = "🍭",
                        description = "Swipe match-3 with 20,145 levels",
                        badgeText = "LEVEL ${user?.candyMatchLevel ?: 1}",
                        badgeColor = CrimsonRed,
                        onClick = { onOpenFunZoneGame(FunZoneGameType.CANDY_MATCH) }
                    )

                    // STICKMAN THIEF
                    FunZoneGameCard(
                        gameType = FunZoneGameType.STICKMAN_THIEF,
                        emoji = "🥷",
                        description = "Stretch hand & steal keys around lasers",
                        badgeText = "LEVEL ${user?.stickmanThiefLevel ?: 1}",
                        badgeColor = GemCyan,
                        onClick = { onOpenFunZoneGame(FunZoneGameType.STICKMAN_THIEF) }
                    )
                }
            }
        }
    }
}

@Composable
fun GamingHero3DCard(
    title: String,
    subtitle: String,
    emoji: String,
    gradientColors: List<Color>,
    badgeText: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PureWhite.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = badgeText,
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = PureWhite
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = PureWhite.copy(alpha = 0.9f)
                )
            }

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(PureWhite.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 32.sp)
            }
        }
    }
}

@Composable
fun MainGameCard3D(
    gameType: MainGameType,
    emoji: String,
    gradient: List<Color>,
    subtitle: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradient))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(emoji, fontSize = 36.sp)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = gameType.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = PureWhite,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = PureWhite.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun FunZoneGameCard(
    gameType: FunZoneGameType,
    emoji: String,
    description: String,
    badgeText: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, LightBorder),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(badgeColor.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = gameType.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MediumGray
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Play",
                tint = MediumGray.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
