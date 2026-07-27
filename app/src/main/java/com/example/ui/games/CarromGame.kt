package com.example.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.models.StakeOption
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

data class CarromCoin(
    var id: Int,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val isWhite: Boolean,
    val isQueen: Boolean = false,
    var isPocketed: Boolean = false
)

@Composable
fun CarromGame(
    stake: StakeOption,
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var coinsPocketedCount by remember { mutableStateOf(0) }
    var gameStatus by remember { mutableStateOf("Position Striker & Drag to Aim & Shoot! 🎯") }
    
    // Board physics dimensions
    val boardSizePx = 320f
    val pocketRadius = 22f
    val coinRadius = 14f
    val strikerRadius = 18f

    // Striker position on baseline
    var strikerX by remember { mutableStateOf(boardSizePx / 2f) }
    var strikerY by remember { mutableStateOf(boardSizePx - 45f) }
    var strikerVx by remember { mutableStateOf(0f) }
    var strikerVy by remember { mutableStateOf(0f) }
    var isStrikerMoving by remember { mutableStateOf(false) }

    // Drag aim line
    var aimTarget by remember { mutableStateOf<Offset?>(null) }
    var shootPower by remember { mutableStateOf(0.5f) }

    // Coins Initialization
    val coinsList = remember {
        mutableStateListOf<CarromCoin>().apply {
            val centerX = boardSizePx / 2f
            val centerY = boardSizePx / 2f
            
            // Red Queen in Center
            add(CarromCoin(0, centerX, centerY, isWhite = false, isQueen = true))
            
            // Circle of White & Black Coins around Queen
            var idCount = 1
            val radius = 28f
            for (i in 0 until 8) {
                val angle = i * (2 * PI / 8)
                val cx = centerX + (radius * cos(angle)).toFloat()
                val cy = centerY + (radius * sin(angle)).toFloat()
                add(CarromCoin(idCount++, cx, cy, isWhite = i % 2 == 0))
            }
        }
    }

    // Pockets
    val pockets = listOf(
        Offset(30f, 30f),                         // Top Left
        Offset(boardSizePx - 30f, 30f),            // Top Right
        Offset(30f, boardSizePx - 30f),            // Bottom Left
        Offset(boardSizePx - 30f, boardSizePx - 30f)// Bottom Right
    )

    // Game Physics Simulation Loop
    LaunchedEffect(isStrikerMoving) {
        if (!isStrikerMoving) return@LaunchedEffect
        
        while (isStrikerMoving) {
            delay(16) // ~60fps
            var anyMoving = false

            // Update Striker
            if (abs(strikerVx) > 0.1f || abs(strikerVy) > 0.1f) {
                anyMoving = true
                strikerX += strikerVx
                strikerY += strikerVy
                strikerVx *= 0.96f // Friction
                strikerVy *= 0.96f

                // Bounce off board walls
                if (strikerX - strikerRadius < 20f || strikerX + strikerRadius > boardSizePx - 20f) {
                    strikerVx = -strikerVx
                }
                if (strikerY - strikerRadius < 20f || strikerY + strikerRadius > boardSizePx - 20f) {
                    strikerVy = -strikerVy
                }
            } else {
                strikerVx = 0f
                strikerVy = 0f
            }

            // Update Coins
            for (coin in coinsList) {
                if (coin.isPocketed) continue

                if (abs(coin.vx) > 0.1f || abs(coin.vy) > 0.1f) {
                    anyMoving = true
                    coin.x += coin.vx
                    coin.y += coin.vy
                    coin.vx *= 0.96f
                    coin.vy *= 0.96f

                    // Walls bounce
                    if (coin.x - coinRadius < 20f || coin.x + coinRadius > boardSizePx - 20f) {
                        coin.vx = -coin.vx
                    }
                    if (coin.y - coinRadius < 20f || coin.y + coinRadius > boardSizePx - 20f) {
                        coin.vy = -coin.vy
                    }

                    // Check Pocket Collisions
                    for (pocket in pockets) {
                        val dist = hypot(coin.x - pocket.x, coin.y - pocket.y)
                        if (dist < pocketRadius) {
                            coin.isPocketed = true
                            coin.vx = 0f
                            coin.vy = 0f
                            coinsPocketedCount++
                            val pts = when {
                                coin.isQueen -> 50
                                coin.isWhite -> 10
                                else -> 5
                            }
                            score += pts
                            gameStatus = if (coin.isQueen) "👑 QUEEN POCKETED! +50 PTS!" else "🪙 COIN POCKETED! +$pts PTS!"
                        }
                    }
                } else {
                    coin.vx = 0f
                    coin.vy = 0f
                }

                // Striker vs Coin Collision
                val distStriker = hypot(strikerX - coin.x, strikerY - coin.y)
                if (distStriker < strikerRadius + coinRadius) {
                    val nx = (coin.x - strikerX) / distStriker
                    val ny = (coin.y - strikerY) / distStriker
                    val kx = strikerVx - coin.vx
                    val ky = strikerVy - coin.vy
                    val p = nx * kx + ny * ky
                    if (p > 0) {
                        strikerVx -= p * nx
                        strikerVy -= p * ny
                        coin.vx += p * nx
                        coin.vy += p * ny
                    }
                }
            }

            // Check if all stopped
            if (!anyMoving) {
                isStrikerMoving = false
                // Reset Striker to baseline
                strikerX = boardSizePx / 2f
                strikerY = boardSizePx - 45f
                aimTarget = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CrispBackground)
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
                Text("CARROM POOL 3D 🏆", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DarkCharcoal)
                Text("STAKE: 🪙 ${stake.entryFee}  |  PRIZE: 🪙 ${stake.prizePool}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GemBlue)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        // Score Card Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureWhite, RoundedCornerShape(16.dp))
                .border(1.dp, SubtleBorder, RoundedCornerShape(16.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("MATCH SCORE", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MediumGray)
                Text("$score PTS", fontWeight = FontWeight.Black, fontSize = 20.sp, color = DarkCharcoal)
            }
            Surface(
                color = GoldenAmber.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "POCKETED: $coinsPocketedCount / 9",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GoldenAmber,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Text(gameStatus, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GemBlue, textAlign = TextAlign.Center)

        // 3D CANVAS CARROM BOARD
        Box(
            modifier = Modifier
                .size(boardSizePx.dp)
                .background(Color(0xFFE5C188), RoundedCornerShape(20.dp)) // Natural Maple Wood Board
                .border(6.dp, Color(0xFF4A2B11), RoundedCornerShape(20.dp)) // Dark Teak Wooden Frame
                .pointerInput(isStrikerMoving) {
                    if (isStrikerMoving) return@pointerInput
                    detectDragGestures(
                        onDragStart = { pos ->
                            aimTarget = pos
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            aimTarget = change.position
                        },
                        onDragEnd = {
                            aimTarget?.let { target ->
                                val dx = target.x - strikerX
                                val dy = target.y - strikerY
                                val len = hypot(dx, dy)
                                if (len > 10f) {
                                    val speed = 18f * shootPower
                                    strikerVx = (dx / len) * speed
                                    strikerVy = (dy / len) * speed
                                    isStrikerMoving = true
                                }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Outer Border Line
                drawRect(
                    color = Color(0xFF8B5A2B),
                    topLeft = Offset(15f, 15f),
                    size = Size(size.width - 30f, size.height - 30f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )

                // Corner Pockets
                for (pocket in pockets) {
                    drawCircle(color = Color(0xFF1E293B), radius = pocketRadius, center = pocket)
                    drawCircle(color = Color.Black, radius = pocketRadius - 4f, center = pocket)
                }

                // Center Rose Circle
                drawCircle(color = Color(0xFFB85D1B), radius = 32f, center = Offset(size.width / 2f, size.height / 2f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))

                // Bottom Baseline
                drawLine(
                    color = Color(0xFF8B5A2B),
                    start = Offset(40f, size.height - 45f),
                    end = Offset(size.width - 40f, size.height - 45f),
                    strokeWidth = 3f
                )

                // Aim Line
                aimTarget?.let { target ->
                    drawLine(
                        color = CrimsonRed,
                        start = Offset(strikerX, strikerY),
                        end = target,
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }

                // Draw Coins
                for (coin in coinsList) {
                    if (coin.isPocketed) continue
                    val cColor = when {
                        coin.isQueen -> CrimsonRed
                        coin.isWhite -> PureWhite
                        else -> Color(0xFF1E293B)
                    }
                    drawCircle(color = cColor, radius = coinRadius, center = Offset(coin.x, coin.y))
                    drawCircle(color = Color.Gray, radius = coinRadius, center = Offset(coin.x, coin.y), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                }

                // Draw Striker
                drawCircle(color = GoldenYellow, radius = strikerRadius, center = Offset(strikerX, strikerY))
                drawCircle(color = PureWhite, radius = strikerRadius - 4f, center = Offset(strikerX, strikerY), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
            }
        }

        // Striker Baseline Slider & Power Charge
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("STRIKER POSITION", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MediumGray)
                Text("POWER: ${(shootPower * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 11.sp, color = GoldenAmber)
            }

            Slider(
                value = strikerX,
                onValueChange = { if (!isStrikerMoving) strikerX = it },
                valueRange = 50f..(boardSizePx - 50f),
                colors = SliderDefaults.colors(thumbColor = GoldenAmber, activeTrackColor = GoldenAmber)
            )
        }

        // Action Buttons
        Button(
            onClick = {
                if (score > 30 || coinsPocketedCount >= 4) {
                    onGameFinished(true, stake.prizePool)
                } else {
                    onGameFinished(false, 0L)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("CLAIM CARROM VICTORY 🏆", fontWeight = FontWeight.Black)
        }
    }
}
