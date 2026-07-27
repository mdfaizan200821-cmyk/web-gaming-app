package com.example.ui.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.models.StakeOption
import com.example.ui.theme.*

@Composable
fun TicTacToeGame(
    stake: StakeOption,
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var gameMessage by remember { mutableStateOf("Your Turn (Player X)") }

    fun checkWin(symbol: String): Boolean {
        val winLines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        return winLines.any { line -> line.all { board[it] == symbol } }
    }

    fun makeBotMove() {
        val emptyIndices = board.indices.filter { board[it].isEmpty() }
        if (emptyIndices.isNotEmpty()) {
            val botMove = emptyIndices.random()
            val newBoard = board.toMutableList()
            newBoard[botMove] = "O"
            board = newBoard
            if (checkWin("O")) {
                onGameFinished(false, 0L)
            } else if (board.none { it.isEmpty() }) {
                gameMessage = "MATCH DRAW!"
            } else {
                isPlayerTurn = true
                gameMessage = "Your Turn (Player X)"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = DarkCharcoal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TIC TAC TOE DUEL", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DarkCharcoal)
                Text("STAKE: 🪙 ${stake.entryFee}", fontSize = 12.sp, color = RoyalPurple)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        Text(gameMessage, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalPurple)

        // 3x3 Grid
        Column(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        val value = board[index]

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    if (value.isEmpty() && isPlayerTurn) {
                                        val newBoard = board.toMutableList()
                                        newBoard[index] = "X"
                                        board = newBoard
                                        if (checkWin("X")) {
                                            onGameFinished(true, stake.prizePool)
                                        } else if (newBoard.none { it.isEmpty() }) {
                                            gameMessage = "MATCH DRAW!"
                                        } else {
                                            isPlayerTurn = false
                                            gameMessage = "Bot Thinking..."
                                            makeBotMove()
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceCardVariant,
                            border = BorderStroke(2.dp, if (value == "X") RoyalPurple else if (value == "O") CrimsonRed else LightBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = value,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (value == "X") RoyalPurple else CrimsonRed
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                board = List(9) { "" }
                isPlayerTurn = true
                gameMessage = "New Round! Your Turn (Player X)"
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("RESET ROUND", fontWeight = FontWeight.Bold)
        }
    }
}
