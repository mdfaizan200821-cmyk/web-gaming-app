package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TalkingCatGame(
    onBack: () -> Unit
) {
    var catAction by remember { mutableStateOf("Idle") } // Idle, PokeHead, TickleBelly, PokeFeet, Feeding, Listening, Repeating
    var isMicActive by remember { mutableStateOf(false) }
    var catSpeechText by remember { mutableStateOf("Meow! Speak to me or touch me!") }
    val coroutineScope = rememberCoroutineScope()

    // Animation values
    val infiniteTransition = rememberInfiniteTransition(label = "tail")
    val tailWiggle by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tail"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PureWhite, SurfaceCardVariant)))
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
                Text("3D TALKING CAT", fontWeight = FontWeight.Black, fontSize = 18.sp, color = GoldenAmber)
                Text("Interactive Voice & Pet Studio", fontSize = 11.sp, color = MediumGray)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        // Cat Speech Bubble
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PureWhite,
            border = BorderStroke(1.5.dp, GoldenYellow),
            shadowElevation = 4.dp
        ) {
            Text(
                text = catSpeechText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarkCharcoal,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }

        // Full Interactive 3D Golden-Brown Cat Canvas
        Box(
            modifier = Modifier
                .size(280.dp, 320.dp)
                .clickable {
                    // Default body click
                    catAction = "TickleBelly"
                    catSpeechText = "Hehehe! That tickles my tummy!"
                    coroutineScope.launch {
                        delay(2000)
                        catAction = "Idle"
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val catGoldenBrown = Color(0xFFC67D33)
                val catInnerEar = Color(0xFFFFB6C1)
                val catEyeGreen = Color(0xFF2E7D32)

                // Tail
                drawArc(
                    color = catGoldenBrown,
                    startAngle = 180f + tailWiggle,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.15f, size.height * 0.65f),
                    size = Size(80f, 120f)
                )

                // Body
                drawRoundRect(
                    color = catGoldenBrown,
                    topLeft = Offset(size.width * 0.3f, size.height * 0.42f),
                    size = Size(size.width * 0.4f, size.height * 0.42f),
                    cornerRadius = CornerRadius(60f, 60f)
                )

                // Head
                val headYOffset = if (catAction == "PokeHead") -15f else 0f
                drawCircle(
                    color = catGoldenBrown,
                    radius = 90f,
                    center = Offset(size.width * 0.5f, size.height * 0.3f + headYOffset)
                )

                // Ears
                // Left Ear
                drawCircle(color = catGoldenBrown, radius = 28f, center = Offset(size.width * 0.35f, size.height * 0.15f + headYOffset))
                drawCircle(color = catInnerEar, radius = 16f, center = Offset(size.width * 0.35f, size.height * 0.15f + headYOffset))
                // Right Ear
                drawCircle(color = catGoldenBrown, radius = 28f, center = Offset(size.width * 0.65f, size.height * 0.15f + headYOffset))
                drawCircle(color = catInnerEar, radius = 16f, center = Offset(size.width * 0.65f, size.height * 0.15f + headYOffset))

                // Eyes
                val eyeHeight = if (catAction == "PokeHead") 6f else 22f
                drawOval(color = PureWhite, topLeft = Offset(size.width * 0.38f, size.height * 0.25f + headYOffset), size = Size(26f, eyeHeight))
                drawOval(color = catEyeGreen, topLeft = Offset(size.width * 0.4f, size.height * 0.26f + headYOffset), size = Size(16f, eyeHeight - 4f))

                drawOval(color = PureWhite, topLeft = Offset(size.width * 0.54f, size.height * 0.25f + headYOffset), size = Size(26f, eyeHeight))
                drawOval(color = catEyeGreen, topLeft = Offset(size.width * 0.56f, size.height * 0.26f + headYOffset), size = Size(16f, eyeHeight - 4f))

                // Nose & Mouth
                drawCircle(color = Color(0xFFFF69B4), radius = 8f, center = Offset(size.width * 0.5f, size.height * 0.32f + headYOffset))

                // Mouth Open if Speaking
                if (catAction == "Repeating") {
                    drawCircle(color = Color.Black, radius = 14f, center = Offset(size.width * 0.5f, size.height * 0.36f + headYOffset))
                }

                // Paws
                drawCircle(color = catGoldenBrown, radius = 22f, center = Offset(size.width * 0.36f, size.height * 0.85f))
                drawCircle(color = catGoldenBrown, radius = 22f, center = Offset(size.width * 0.64f, size.height * 0.85f))
            }
        }

        // Interactive Touch Zone Controls: POKE HEAD | PET | FEED MILK
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    catAction = "PokeHead"
                    catSpeechText = "Ouch! Stars in my eyes! ⭐"
                    coroutineScope.launch {
                        delay(2000)
                        catAction = "Idle"
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("POKE 🐱", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarkCharcoal)
            }

            Button(
                onClick = {
                    catAction = "Feeding"
                    catSpeechText = "Yum! Delicious fresh milk & fish! 🐟"
                    coroutineScope.launch {
                        delay(2000)
                        catAction = "Idle"
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = GemCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("FEED 🥛", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PureWhite)
            }

            Button(
                onClick = {
                    catAction = "TickleBelly"
                    catSpeechText = "Purrrr... Purrrr... I love you! ❤️"
                    coroutineScope.launch {
                        delay(2000)
                        catAction = "Idle"
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("PET ❤️", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PureWhite)
            }
        }

        // Microphone Voice Repetition Toggle
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PureWhite,
            border = BorderStroke(1.5.dp, if (isMicActive) GoldenAmber else LightBorder),
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isMicActive) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Mic",
                        tint = if (isMicActive) GoldenAmber else MediumGray,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isMicActive) "MIC LISTENING ACTIVE" else "MIC OFF (SIMULATION MODE)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Cat repeats your voice in pitch-shifted audio!",
                            fontSize = 11.sp,
                            color = MediumGray
                        )
                    }
                }

                Switch(
                    checked = isMicActive,
                    onCheckedChange = {
                        isMicActive = it
                        if (isMicActive) {
                            catAction = "Listening"
                            catSpeechText = "Listening to you... Speak now!"
                        } else {
                            catAction = "Idle"
                            catSpeechText = "Meow! Touch buttons to play!"
                        }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = GoldenAmber)
                )
            }
        }
    }
}
