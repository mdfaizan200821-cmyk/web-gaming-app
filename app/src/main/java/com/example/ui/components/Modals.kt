package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.database.UserEntity
import com.example.ui.models.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResultModalData
import com.example.ui.viewmodel.ChestOpeningResult
import kotlin.random.Random

// 1. PROFILE CUSTOMIZATION MODAL
@Composable
fun ProfileCustomizationModal(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onEquipAvatar: (String) -> Unit,
    onEquipFrame: (String) -> Unit
) {
    var selectedSection by remember { mutableStateOf(0) } // 0: Avatars, 1: Aura Frames, 2: Stats

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = PureWhite,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROFILE & CUSTOMIZATION",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkCharcoal
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MediumGray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Avatar Live Equipped Preview Box
                val equippedFrame = PRESET_FRAMES.find { it.id == user?.equippedFrameId } ?: PRESET_FRAMES.last()
                val equippedAvatar = PRESET_AVATARS.find { it.id == user?.equippedAvatarId } ?: PRESET_AVATARS.first()

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(equippedFrame.glowColors),
                                radius = size.width / 2f
                            )
                        }
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(PureWhite)
                        .border(3.dp, PureWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(equippedAvatar.primaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = equippedAvatar.iconSymbol,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 38.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user?.name ?: "Master Gamer",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = DarkCharcoal
                )
                Text(
                    text = "AURA FRAME: ${equippedFrame.name.uppercase()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GoldenAmber
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs: AVATARS | AURA FRAMES | STATS
                TabRow(
                    selectedTabIndex = selectedSection,
                    containerColor = SurfaceCardVariant,
                    contentColor = RoyalPurple,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        text = { Text("AVATARS", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        text = { Text("DIVINE FRAMES", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedSection == 2,
                        onClick = { selectedSection = 2 },
                        text = { Text("STATS", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedSection) {
                        0 -> {
                            // Avatars Grid
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                PRESET_AVATARS.forEach { avatar ->
                                    val isEquipped = user?.equippedAvatarId == avatar.id
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isEquipped) ElectricIndigo.copy(alpha = 0.08f) else SurfaceCardVariant,
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (isEquipped) RoyalPurple else LightBorder
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(avatar.primaryColor, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(avatar.iconSymbol, color = PureWhite, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(avatar.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                                                    Text(avatar.badge, fontSize = 11.sp, color = MediumGray)
                                                }
                                            }

                                            Button(
                                                onClick = { onEquipAvatar(avatar.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isEquipped) EmeraldGreen else RoyalPurple
                                                ),
                                                enabled = !isEquipped,
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text(
                                                    if (isEquipped) "EQUIPPED" else "EQUIP",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Aura Frames Grid
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                PRESET_FRAMES.forEach { frame ->
                                    val isEquipped = user?.equippedFrameId == frame.id
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isEquipped) GoldenYellow.copy(alpha = 0.08f) else SurfaceCardVariant,
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (isEquipped) GoldenYellow else LightBorder
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .drawBehind {
                                                            drawCircle(
                                                                brush = Brush.sweepGradient(frame.glowColors)
                                                            )
                                                        }
                                                        .padding(3.dp)
                                                        .clip(CircleShape)
                                                        .background(PureWhite),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("✨", fontSize = 16.sp)
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(frame.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                                                    Text(frame.description, fontSize = 11.sp, color = MediumGray)
                                                }
                                            }

                                            Button(
                                                onClick = { onEquipFrame(frame.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isEquipped) GoldenAmber else RoyalPurple
                                                ),
                                                enabled = !isEquipped,
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text(
                                                    if (isEquipped) "EQUIPPED" else "EQUIP",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Stats View
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    StatBox("Level", "${user?.level ?: 12}", RoyalPurple, Modifier.weight(1f))
                                    StatBox("Total Matches", "${user?.totalMatches ?: 54}", GemCyan, Modifier.weight(1f))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    StatBox("Victories", "${user?.wins ?: 42}", EmeraldGreen, Modifier.weight(1f))
                                    StatBox("Defeats", "${user?.losses ?: 12}", CrimsonRed, Modifier.weight(1f))
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SurfaceCardVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("WIN RATE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MediumGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val total = (user?.totalMatches ?: 54).coerceAtLeast(1)
                                        val rate = ((user?.wins ?: 42).toFloat() / total * 100).toInt()
                                        Text("$rate%", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, color = EmeraldGreen)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { rate / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = EmeraldGreen,
                                            trackColor = LightBorder,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color, modifier: Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label.uppercase(), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MediumGray)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = color)
        }
    }
}

// 2. VICTORY & DEFEAT ANNOUNCEMENT POPUP MODAL
@Composable
fun WinDefeatModal(
    result: ResultModalData,
    onDismiss: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .scale(scale)
                    .clip(RoundedCornerShape(28.dp)),
                color = PureWhite,
                shadowElevation = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Particle canvas effect for WIN
                    if (result.isWin) {
                        Canvas(modifier = Modifier.size(80.dp)) {
                            for (i in 0..12) {
                                val angle = i * 30f
                                val rad = Math.toRadians(angle.toDouble())
                                val dx = (size.width / 2) + (30 * Math.cos(rad)).toFloat()
                                val dy = (size.height / 2) + (30 * Math.sin(rad)).toFloat()
                                drawCircle(
                                    color = if (i % 2 == 0) GoldenYellow else RoyalPurple,
                                    radius = 6f,
                                    center = Offset(dx, dy)
                                )
                            }
                        }
                    }

                    Text(
                        text = result.title, // "WIN" or "OOPS"
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = if (result.isWin) GoldenYellow else CrimsonRed,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = result.subtitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkCharcoal,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (result.isWin && result.rewardCoins > 0) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GoldenYellow.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, GoldenYellow)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🪙 REWARD: +${result.rewardCoins} COINS", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DarkCharcoal)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (result.isWin) GoldenAmber else RoyalPurple
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = if (result.isWin) "CLAIM & CONTINUE" else "RETRY MATCH",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = PureWhite
                        )
                    }
                }
            }
        }
    }
}

// 3. FULL-SCREEN HORIZONTAL SNAP STAKE SELECTION CAROUSEL MODAL
@Composable
fun StakeSelectionCarouselModal(
    gameType: MainGameType,
    userCoins: Long,
    onClose: () -> Unit,
    onSelectStake: (StakeOption) -> Unit
) {
    val listState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modal Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = gameType.title.uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "SELECT STAKE PLATFORM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkCharcoal)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // User Balance Banner
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceCardVariant,
                    border = BorderStroke(1.dp, GoldenYellow),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("YOUR COIN BALANCE:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MediumGray)
                        Text("🪙 ${String.format("%,d", userCoins)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DarkCharcoal)
                    }
                }

                Spacer(modifier = Modifier.weight(0.2f))

                // Horizontal Snap Stake Carousel
                LazyRow(
                    state = listState,
                    flingBehavior = snapBehavior,
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(STAKE_CAROUSEL_OPTIONS) { stake ->
                        val canAfford = userCoins >= stake.entryFee

                        Surface(
                            modifier = Modifier
                                .width(280.dp)
                                .height(380.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = PureWhite,
                            border = BorderStroke(
                                2.dp,
                                if (canAfford) RoyalPurple else LightBorder
                            ),
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Platform Rank Badge
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (canAfford) RoyalPurple else MediumGray,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = stake.platformRank,
                                        color = PureWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    text = stake.title,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = DarkCharcoal,
                                    textAlign = TextAlign.Center
                                )

                                // Entry Fee Big Display
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("ENTRY FEE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MediumGray)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🪙 ", fontSize = 20.sp)
                                        Text(
                                            text = String.format("%,d", stake.entryFee),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 28.sp,
                                            color = DarkCharcoal
                                        )
                                    }
                                }

                                // Prize Pool Box
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldenYellow.copy(alpha = 0.12f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("ESTIMATED PRIZE POOL", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = GoldenAmber)
                                        Text("🪙 ${String.format("%,d", stake.prizePool)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DarkCharcoal)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.People, contentDescription = null, tint = MediumGray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${stake.onlinePlayers} players online", fontSize = 12.sp, color = MediumGray)
                                }

                                Button(
                                    onClick = { onSelectStake(stake) },
                                    enabled = canAfford,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RoyalPurple,
                                        disabledContainerColor = LightBorder
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(14.dp)
                                ) {
                                    Text(
                                        text = if (canAfford) "JOIN TABLE" else "LOW COINS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = if (canAfford) PureWhite else MediumGray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.2f))

                Text(
                    text = "SWIPE HORIZONTALLY TO SELECT HIGH STAKES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MediumGray
                )
            }
        }
    }
}

// 4. WATCH AD SIMULATION MODAL
@Composable
fun AdWatchModal(
    secondsRemaining: Int
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            color = DarkCharcoal
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("SPONSORED REWARD VIDEO", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(RoyalPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📺", fontSize = 48.sp)
                        Text("WATCHING SPONSORED GAME TRAILER", color = PureWhite, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        progress = { secondsRemaining / 5f },
                        color = GoldenYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Reward in $secondsRemaining seconds...", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 5. CHEST OPENING POPUP RESULT
@Composable
fun ChestOpeningResultModal(
    result: ChestOpeningResult,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎁 UNBOXING SUCCESS!", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = RoyalPurple)
                Spacer(modifier = Modifier.height(12.dp))

                Text("✨ ${result.chestName.uppercase()} OPENED!", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceCardVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🪙 +${result.coinsWon} Coins", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = DarkCharcoal)
                        Text("💎 +${result.gemsWon} Gems", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = GemCyan)
                        if (result.itemUnlocked != null) {
                            Text("👑 UNLOCKED: ${result.itemUnlocked}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldenAmber)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("COLLECT ALL LOOT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
