package com.example.ui.games

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.hypot

@Composable
fun StickmanThiefGame(
    level: Int,
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var handPathPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var currentTouchPos by remember { mutableStateOf<Offset?>(null) }
    var gameStatus by remember { mutableStateOf("Drag Thief's Arm as a Yellow Line around laser beams to steal the Key! 🔑") }
    var isCaughtByLaser by remember { mutableStateOf(false) }

    val startPos = Offset(100f, 400f) // Stickman Shoulder
    val keyPos = Offset(650f, 180f)   // Key Target
    val laserPos1 = Offset(350f, 280f)// Obstacle Laser 1
    val laserPos2 = Offset(500f, 450f)// Obstacle Laser 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CrispBackground)
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
                Text("STICKMAN THIEF PUZZLE 🥷", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DarkCharcoal)
                Text("LEVEL $level / 20,145", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GemBlue)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        Text(
            text = gameStatus,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isCaughtByLaser) CrimsonRed else GemBlue
        )

        // Stretch Yellow Arm Canvas Arena
        Box(
            modifier = Modifier
                .aspectRatio(0.85f)
                .fillMaxWidth()
                .background(PureWhite, RoundedCornerShape(20.dp))
                .border(2.dp, LightBorder, RoundedCornerShape(20.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { pos ->
                            handPathPoints = listOf(startPos, pos)
                            currentTouchPos = pos
                            isCaughtByLaser = false
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val pos = change.position
                            currentTouchPos = pos
                            val newPoints = handPathPoints.toMutableList()
                            newPoints.add(pos)
                            handPathPoints = newPoints

                            // Check collision with Red Laser Beam Traps
                            val distToLaser1 = hypot(pos.x - laserPos1.x, pos.y - laserPos1.y)
                            val distToLaser2 = hypot(pos.x - laserPos2.x, pos.y - laserPos2.y)
                            if (distToLaser1 < 50f || distToLaser2 < 50f) {
                                isCaughtByLaser = true
                                gameStatus = "⚡ ALARM TRIGGERED! LASER BEAM DETECTED!"
                            }

                            // Check collision with Key Target
                            val distToKey = hypot(pos.x - keyPos.x, pos.y - keyPos.y)
                            if (distToKey < 50f && !isCaughtByLaser) {
                                onGameFinished(true, 1500L + (level * 20))
                            }
                        },
                        onDragEnd = {
                            if (isCaughtByLaser) {
                                onGameFinished(false, 0L)
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw Stickman Body
                drawCircle(color = DarkCharcoal, radius = 24f, center = Offset(startPos.x - 30f, startPos.y - 40f))
                drawLine(color = DarkCharcoal, start = Offset(startPos.x - 30f, startPos.y - 16f), end = Offset(startPos.x - 30f, startPos.y + 60f), strokeWidth = 8f)

                // Draw Red Laser Obstacle Traps
                drawLine(color = CrimsonRed, start = Offset(laserPos1.x - 60f, laserPos1.y - 60f), end = Offset(laserPos1.x + 60f, laserPos1.y + 60f), strokeWidth = 10f)
                drawCircle(color = CrimsonRed, radius = 18f, center = laserPos1)

                drawLine(color = CrimsonRed, start = Offset(laserPos2.x - 60f, laserPos2.y + 60f), end = Offset(laserPos2.x + 60f, laserPos2.y - 60f), strokeWidth = 10f)
                drawCircle(color = CrimsonRed, radius = 18f, center = laserPos2)

                // Draw Target Golden Key 🔑
                drawCircle(color = GoldenYellow, radius = 28f, center = keyPos)

                // Draw Dynamic Elastic Yellow Stretched Arm Line
                if (handPathPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(startPos.x, startPos.y)
                        handPathPoints.forEach { pt ->
                            lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = if (isCaughtByLaser) CrimsonRed else GoldenYellow,
                        style = Stroke(width = 14f, cap = StrokeCap.Round)
                    )

                    // Thief Hand Grabber Icon
                    currentTouchPos?.let { pos ->
                        drawCircle(color = GoldenAmber, radius = 18f, center = pos)
                    }
                }
            }
        }

        Button(
            onClick = { onGameFinished(true, 1500L) },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("PASS THIEF LEVEL 🔑 (AUTO STEAL)", fontWeight = FontWeight.Bold)
        }
    }
}
