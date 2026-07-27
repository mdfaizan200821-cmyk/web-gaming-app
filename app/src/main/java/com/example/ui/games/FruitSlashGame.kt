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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

data class FlyingFruit(
    val id: Int,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val typeEmoji: String,
    val isBomb: Boolean,
    var isSliced: Boolean = false
)

@Composable
fun FruitSlashGame(
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var combos by remember { mutableStateOf(0) }
    var gameStatus by remember { mutableStateOf("Swipe finger across screen to SLASH FRUITS! Avoid Bombs 💣!") }
    var isGameOver by remember { mutableStateOf(false) }

    var bladeTrailPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    val flyingFruits = remember { mutableStateListOf<FlyingFruit>() }

    // Game Loop - Physics Gravity & Fruit Spawning
    LaunchedEffect(isGameOver) {
        if (isGameOver) return@LaunchedEffect
        var frameCount = 0
        var fruitIdCounter = 1

        while (!isGameOver) {
            delay(20) // ~50fps
            frameCount++

            // Launch Fruits
            if (frameCount % 40 == 0) {
                val emojis = listOf("🍉", "🍎", "🍌", "🍊", "🍍", "💣")
                val isBomb = Random.nextInt(1, 6) == 1
                val emoji = if (isBomb) "💣" else emojis.filter { it != "💣" }.random()
                
                val startX = Random.nextFloat() * 600f + 100f
                val startY = 850f
                val vx = (Random.nextFloat() * 12f) - 6f
                val vy = -(Random.nextFloat() * 6f + 16f) // Launch upwards
                flyingFruits.add(FlyingFruit(fruitIdCounter++, startX, startY, vx, vy, emoji, isBomb))
            }

            // Update Physics Position
            val iterator = flyingFruits.iterator()
            while (iterator.hasNext()) {
                val f = iterator.next()
                f.x += f.vx
                f.y += f.vy
                f.vy += 0.55f // Gravity pulling down

                // Remove offscreen bottom
                if (f.y > 950f) {
                    iterator.remove()
                }
            }

            // Target Score Check
            if (score >= 100) {
                isGameOver = true
                gameStatus = "🏆 100 PTS REACHED! FRUIT SLASH CHAMPION!"
                onGameFinished(true, 2500L + (combos * 50))
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
                Text("FRUIT SLASH ⚔️", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DarkCharcoal)
                Text("SCORE: $score PTS  |  COMBOS: $combos x2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GemBlue)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        Text(gameStatus, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GemBlue)

        // BLADE SWIPE ARENA CANVAS
        Box(
            modifier = Modifier
                .aspectRatio(0.85f)
                .fillMaxWidth()
                .background(Color(0xFF0F172A), RoundedCornerShape(20.dp))
                .border(2.dp, LightBorder, RoundedCornerShape(20.dp))
                .pointerInput(isGameOver) {
                    if (isGameOver) return@pointerInput
                    detectDragGestures(
                        onDragStart = { pos ->
                            bladeTrailPoints = listOf(pos)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val pos = change.position
                            val newTrail = bladeTrailPoints.takeLast(12).toMutableList()
                            newTrail.add(pos)
                            bladeTrailPoints = newTrail

                            // Check Slicing Collision with Flying Fruits
                            for (fruit in flyingFruits) {
                                if (fruit.isSliced) continue
                                val dist = hypot(pos.x - fruit.x, pos.y - fruit.y)
                                if (dist < 50f) {
                                    if (fruit.isBomb) {
                                        isGameOver = true
                                        gameStatus = "💣 BOOM! YOU SLICED A BOMB!"
                                        onGameFinished(false, 0L)
                                        break
                                    } else {
                                        fruit.isSliced = true
                                        score += 10
                                        combos++
                                        gameStatus = "⚡ SLICED ${fruit.typeEmoji}! +10 PTS!"
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            bladeTrailPoints = emptyList()
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw Flying Fruits
                for (fruit in flyingFruits) {
                    if (fruit.isSliced) continue
                    drawCircle(color = GoldenYellow.copy(alpha = 0.15f), radius = 35f, center = Offset(fruit.x, fruit.y))
                }

                // Draw Glowing Blade Slash Trail
                if (bladeTrailPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(bladeTrailPoints.first().x, bladeTrailPoints.first().y)
                        bladeTrailPoints.forEach { pt ->
                            lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = GemCyan,
                        style = Stroke(width = 10f, cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = path,
                        color = PureWhite,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }
            }

            // Render Fruit Emojis over canvas
            for (fruit in flyingFruits) {
                if (fruit.isSliced) continue
                Box(
                    modifier = Modifier.offset(
                        x = (fruit.x / 2.5f).dp,
                        y = (fruit.y / 2.5f).dp
                    )
                ) {
                    Text(
                        text = fruit.typeEmoji,
                        fontSize = 32.sp
                    )
                }
            }
        }

        Button(
            onClick = { onGameFinished(true, 1800L) },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("AUTO SLASH VICTORY 🏆", fontWeight = FontWeight.Black)
        }
    }
}
