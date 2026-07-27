package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class RunnerObstacle(
    val id: Int,
    val lane: Int, // -1 (Left), 0 (Center), 1 (Right)
    var zProgress: Float, // 0.0 (Far) to 1.0 (Player)
    val isTrain: Boolean
)

data class RunnerCoinItem(
    val id: Int,
    val lane: Int,
    var zProgress: Float,
    var isCollected: Boolean = false
)

@Composable
fun SubwayRunnerGame(
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var playerLane by remember { mutableStateOf(0) } // -1, 0, 1
    var isJumping by remember { mutableStateOf(false) }
    var isSliding by remember { mutableStateOf(false) }
    
    var distanceMeters by remember { mutableStateOf(0) }
    var coinsCollected by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var gameStatus by remember { mutableStateOf("Dodge trains & obstacles! Collect Gold Coins! 🪙") }

    val obstacles = remember { mutableStateListOf<RunnerObstacle>() }
    val coinItems = remember { mutableStateListOf<RunnerCoinItem>() }

    // Game Loop
    LaunchedEffect(isGameOver) {
        if (isGameOver) return@LaunchedEffect
        var frameCount = 0
        var obstacleIdCounter = 1
        var coinIdCounter = 1

        while (!isGameOver) {
            delay(30) // ~30fps
            frameCount++
            distanceMeters += 2

            // Spawn Obstacles
            if (frameCount % 40 == 0) {
                val lane = listOf(-1, 0, 1).random()
                obstacles.add(RunnerObstacle(obstacleIdCounter++, lane, 0.0f, isTrain = (1..2).random() == 1))
            }

            // Spawn Coins
            if (frameCount % 20 == 0) {
                val lane = listOf(-1, 0, 1).random()
                coinItems.add(RunnerCoinItem(coinIdCounter++, lane, 0.0f))
            }

            // Move Obstacles towards player
            val iterator = obstacles.iterator()
            while (iterator.hasNext()) {
                val obs = iterator.next()
                obs.zProgress += 0.04f

                // Collision check near player (zProgress ~ 0.85 to 0.95)
                if (obs.zProgress in 0.82f..0.98f && obs.lane == playerLane) {
                    if (obs.isTrain && !isSliding) {
                        isGameOver = true
                        gameStatus = "💥 HIT BY TRAIN! GAME OVER"
                        onGameFinished(false, 0L)
                        break
                    } else if (!obs.isTrain && !isJumping) {
                        isGameOver = true
                        gameStatus = "🛑 CRASHED INTO BARRIER! GAME OVER"
                        onGameFinished(false, 0L)
                        break
                    }
                }

                if (obs.zProgress > 1.1f) {
                    iterator.remove()
                }
            }

            // Move Coins
            val coinIter = coinItems.iterator()
            while (coinIter.hasNext()) {
                val coin = coinIter.next()
                coin.zProgress += 0.04f

                if (coin.zProgress in 0.8f..1.0f && coin.lane == playerLane && !coin.isCollected) {
                    coin.isCollected = true
                    coinsCollected++
                }

                if (coin.zProgress > 1.1f) {
                    coinIter.remove()
                }
            }

            // Victory Target
            if (distanceMeters >= 500) {
                isGameOver = true
                gameStatus = "🏆 500M RUN COMPLETE! SUBWAY RUNNER CHAMPION!"
                onGameFinished(true, 2500L + (coinsCollected * 20))
            }
        }
    }

    // Reset Jump / Slide Timers
    LaunchedEffect(isJumping) {
        if (isJumping) {
            delay(600)
            isJumping = false
        }
    }
    LaunchedEffect(isSliding) {
        if (isSliding) {
            delay(600)
            isSliding = false
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
                Text("SUBWAY 3D RUNNER 🏃", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DarkCharcoal)
                Text("DISTANCE: $distanceMeters m  |  COINS: $coinsCollected 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GemBlue)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        Text(gameStatus, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GemBlue)

        // 3D 3-LANE PERSPECTIVE CANVAS
        Box(
            modifier = Modifier
                .aspectRatio(0.85f)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))), RoundedCornerShape(20.dp))
                .border(2.dp, LightBorder, RoundedCornerShape(20.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Vanishing point in distant center
                val vpX = width / 2f
                val vpY = height * 0.25f

                // Draw 3 Subway Track Lines radiating from VP
                val leftEdgeX = width * 0.1f
                val rightEdgeX = width * 0.9f
                val bottomY = height * 0.95f

                // Track borders
                drawLine(color = Color(0xFF475569), start = Offset(vpX - 20f, vpY), end = Offset(leftEdgeX, bottomY), strokeWidth = 4f)
                drawLine(color = Color(0xFF475569), start = Offset(vpX + 20f, vpY), end = Offset(rightEdgeX, bottomY), strokeWidth = 4f)

                // Lane divider tracks
                val lane1X = width * 0.37f
                val lane2X = width * 0.63f
                drawLine(color = GoldenYellow.copy(alpha = 0.5f), start = Offset(vpX - 7f, vpY), end = Offset(lane1X, bottomY), strokeWidth = 2f)
                drawLine(color = GoldenYellow.copy(alpha = 0.5f), start = Offset(vpX + 7f, vpY), end = Offset(lane2X, bottomY), strokeWidth = 2f)

                // Draw Coins
                for (c in coinItems) {
                    if (c.isCollected) continue
                    val progress = c.zProgress
                    val laneXOffset = when (c.lane) {
                        -1 -> -width * 0.28f * progress
                        1 -> width * 0.28f * progress
                        else -> 0f
                    }
                    val cx = vpX + laneXOffset
                    val cy = vpY + (bottomY - vpY) * progress
                    val coinRadius = 10f + (16f * progress)

                    drawCircle(color = GoldenYellow, radius = coinRadius, center = Offset(cx, cy))
                    drawCircle(color = PureWhite, radius = coinRadius * 0.5f, center = Offset(cx, cy))
                }

                // Draw Obstacles
                for (obs in obstacles) {
                    val progress = obs.zProgress
                    val laneXOffset = when (obs.lane) {
                        -1 -> -width * 0.28f * progress
                        1 -> width * 0.28f * progress
                        else -> 0f
                    }
                    val ox = vpX + laneXOffset
                    val oy = vpY + (bottomY - vpY) * progress
                    val obsWidth = (30f + 60f * progress)
                    val obsHeight = if (obs.isTrain) (40f + 80f * progress) else (20f + 40f * progress)

                    val color = if (obs.isTrain) GemBlue else CrimsonRed
                    drawRect(
                        color = color,
                        topLeft = Offset(ox - obsWidth / 2f, oy - obsHeight),
                        size = Size(obsWidth, obsHeight)
                    )
                }

                // Draw Runner Player Character
                val playerXOffset = when (playerLane) {
                    -1 -> -width * 0.28f
                    1 -> width * 0.28f
                    else -> 0f
                }
                val px = vpX + playerXOffset
                val py = bottomY - 30f - (if (isJumping) 70f else 0f)
                val pHeight = if (isSliding) 25f else 55f

                // Runner Body
                drawCircle(color = GoldenAmber, radius = 18f, center = Offset(px, py - pHeight))
                drawRect(color = ElectricIndigo, topLeft = Offset(px - 14f, py - pHeight + 10f), size = Size(28f, pHeight - 10f))
            }
        }

        // SWIPE & BUTTON CONTROLS: LEFT | JUMP | SLIDE | RIGHT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { if (playerLane > -1) playerLane-- },
                colors = ButtonDefaults.buttonColors(containerColor = GemBlue),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("◄ LEFT", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { isJumping = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("▲ JUMP", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { isSliding = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("▼ SLIDE", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { if (playerLane < 1) playerLane++ },
                colors = ButtonDefaults.buttonColors(containerColor = GemBlue),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("RIGHT ►", fontWeight = FontWeight.Bold)
            }
        }
    }
}
