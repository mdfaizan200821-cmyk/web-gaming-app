package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.models.StakeOption
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChessPiece(val symbol: String, val isWhite: Boolean, val type: String)

@Composable
fun ChessGame(
    stake: StakeOption,
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var selectedRow by remember { mutableStateOf<Int?>(null) }
    var selectedCol by remember { mutableStateOf<Int?>(null) }
    var validTargetMoves by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var isWhiteTurn by remember { mutableStateOf(true) }
    var gameStatus by remember { mutableStateOf("Your Turn (White Pieces) - Tap piece to see valid moves") }
    var capturedBlackPieces by remember { mutableStateOf<List<String>>(emptyList()) }
    var capturedWhitePieces by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Pulse animation for valid move highlights
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Initial 8x8 Board State
    var boardState by remember {
        mutableStateOf(
            Array(8) { row ->
                Array(8) { col ->
                    when (row) {
                        0 -> when (col) {
                            0, 7 -> ChessPiece("♜", false, "ROOK")
                            1, 6 -> ChessPiece("♞", false, "KNIGHT")
                            2, 5 -> ChessPiece("♝", false, "BISHOP")
                            3 -> ChessPiece("♛", false, "QUEEN")
                            4 -> ChessPiece("♚", false, "KING")
                            else -> null
                        }
                        1 -> ChessPiece("♟", false, "PAWN")
                        6 -> ChessPiece("♙", true, "PAWN")
                        7 -> when (col) {
                            0, 7 -> ChessPiece("♖", true, "ROOK")
                            1, 6 -> ChessPiece("♘", true, "KNIGHT")
                            2, 5 -> ChessPiece("♗", true, "BISHOP")
                            3 -> ChessPiece("♕", true, "QUEEN")
                            4 -> ChessPiece("♔", true, "KING")
                            else -> null
                        }
                        else -> null
                    }
                }
            }
        )
    }

    // Function to calculate valid target squares for a selected piece
    fun calculateValidMoves(row: Int, col: Int): List<Pair<Int, Int>> {
        val piece = boardState[row][col] ?: return emptyList()
        val moves = mutableListOf<Pair<Int, Int>>()

        fun isInside(r: Int, c: Int) = r in 0..7 && c in 0..7

        fun checkRay(dr: Int, dc: Int) {
            var r = row + dr
            var c = col + dc
            while (isInside(r, c)) {
                val target = boardState[r][c]
                if (target == null) {
                    moves.add(r to c)
                } else {
                    if (target.isWhite != piece.isWhite) {
                        moves.add(r to c) // Capture enemy
                    }
                    break // Own or enemy piece blocks ray
                }
                r += dr
                c += dc
            }
        }

        when (piece.type) {
            "PAWN" -> {
                val dir = if (piece.isWhite) -1 else 1
                val startRow = if (piece.isWhite) 6 else 1
                // Forward 1
                if (isInside(row + dir, col) && boardState[row + dir][col] == null) {
                    moves.add((row + dir) to col)
                    // Forward 2 if starting row
                    if (row == startRow && boardState[row + (2 * dir)][col] == null) {
                        moves.add((row + (2 * dir)) to col)
                    }
                }
                // Diagonals capture
                for (dc in listOf(-1, 1)) {
                    val r = row + dir
                    val c = col + dc
                    if (isInside(r, c)) {
                        val target = boardState[r][c]
                        if (target != null && target.isWhite != piece.isWhite) {
                            moves.add(r to c)
                        }
                    }
                }
            }
            "KNIGHT" -> {
                val knightOffsets = listOf(
                    -2 to -1, -2 to 1, -1 to -2, -1 to 2,
                    1 to -2, 1 to 2, 2 to -1, 2 to 1
                )
                for ((dr, dc) in knightOffsets) {
                    val r = row + dr
                    val c = col + dc
                    if (isInside(r, c)) {
                        val target = boardState[r][c]
                        if (target == null || target.isWhite != piece.isWhite) {
                            moves.add(r to c)
                        }
                    }
                }
            }
            "BISHOP" -> {
                checkRay(-1, -1); checkRay(-1, 1); checkRay(1, -1); checkRay(1, 1)
            }
            "ROOK" -> {
                checkRay(-1, 0); checkRay(1, 0); checkRay(0, -1); checkRay(0, 1)
            }
            "QUEEN" -> {
                checkRay(-1, -1); checkRay(-1, 1); checkRay(1, -1); checkRay(1, 1)
                checkRay(-1, 0); checkRay(1, 0); checkRay(0, -1); checkRay(0, 1)
            }
            "KING" -> {
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        if (dr == 0 && dc == 0) continue
                        val r = row + dr
                        val c = col + dc
                        if (isInside(r, c)) {
                            val target = boardState[r][c]
                            if (target == null || target.isWhite != piece.isWhite) {
                                moves.add(r to c)
                            }
                        }
                    }
                }
            }
        }
        return moves
    }

    // Bot Move Logic
    fun executeBotMove() {
        coroutineScope.launch {
            delay(700)
            val blackPiecesMoves = mutableListOf<Triple<Pair<Int, Int>, Pair<Int, Int>, ChessPiece>>()
            for (r in 0..7) {
                for (c in 0..7) {
                    val p = boardState[r][c]
                    if (p != null && !p.isWhite) {
                        val moves = calculateValidMoves(r, c)
                        for (m in moves) {
                            blackPiecesMoves.add(Triple(r to c, m, p))
                        }
                    }
                }
            }
            if (blackPiecesMoves.isNotEmpty()) {
                val chosen = blackPiecesMoves.random()
                val (from, to, p) = chosen
                val newBoard = boardState.map { it.clone() }.toTypedArray()
                val targetPiece = newBoard[to.first][to.second]
                if (targetPiece != null && targetPiece.isWhite) {
                    capturedWhitePieces = capturedWhitePieces + targetPiece.symbol
                }
                newBoard[to.first][to.second] = p
                newBoard[from.first][from.second] = null
                boardState = newBoard
                isWhiteTurn = true
                gameStatus = "Your Turn (White Pieces) - Select piece to move"
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
        // Top Header with Stake
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = DarkCharcoal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ROYAL CHESS DUEL ♟️", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DarkCharcoal)
                Text("STAKE: 🪙 ${stake.entryFee}  |  PRIZE: 🪙 ${stake.prizePool}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GemBlue)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        // Captured Pieces Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureWhite, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("BOT 🤖: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MediumGray)
                Text(capturedWhitePieces.joinToString(" "), fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("YOU 👑: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MediumGray)
                Text(capturedBlackPieces.joinToString(" "), fontSize = 14.sp)
            }
        }

        // Game Status Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PureWhite,
            border = BorderStroke(1.dp, SubtleBorder),
            shadowElevation = 2.dp
        ) {
            Text(
                text = gameStatus,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isWhiteTurn) GemBlue else Color(0xFFD97706),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        // 8x8 REALISTIC WOOD CHESSBOARD WITH GLOWING VALID MOVE HIGHLIGHTS
        Surface(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp,
            border = BorderStroke(3.dp, Color(0xFF5C3A21)) // Rich Mahogany border
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0 until 8) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0 until 8) {
                                val isDarkSquare = (row + col) % 2 == 1
                                val isSelected = selectedRow == row && selectedCol == col
                                val isValidTarget = validTargetMoves.contains(row to col)

                                val tileColor = when {
                                    isSelected -> Color(0xFFFBBF24) // Golden Highlight
                                    isDarkSquare -> Color(0xFFB87333) // Warm Copper Wood
                                    else -> Color(0xFFF5E6CA) // Warm Beech Wood
                                }

                                val piece = boardState[row][col]

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(tileColor)
                                        .clickable {
                                            if (!isWhiteTurn) return@clickable

                                            if (selectedRow == null) {
                                                if (piece != null && piece.isWhite) {
                                                    selectedRow = row
                                                    selectedCol = col
                                                    validTargetMoves = calculateValidMoves(row, col)
                                                }
                                            } else {
                                                val r1 = selectedRow!!
                                                val c1 = selectedCol!!

                                                if (isValidTarget) {
                                                    val newBoard = boardState.map { it.clone() }.toTypedArray()
                                                    val captured = newBoard[row][col]
                                                    if (captured != null && !captured.isWhite) {
                                                        capturedBlackPieces = capturedBlackPieces + captured.symbol
                                                    }
                                                    newBoard[row][col] = newBoard[r1][c1]
                                                    newBoard[r1][c1] = null
                                                    boardState = newBoard
                                                    selectedRow = null
                                                    selectedCol = null
                                                    validTargetMoves = emptyList()
                                                    isWhiteTurn = false
                                                    gameStatus = "Bot 🤖 is thinking..."
                                                    executeBotMove()
                                                } else if (piece != null && piece.isWhite) {
                                                    selectedRow = row
                                                    selectedCol = col
                                                    validTargetMoves = calculateValidMoves(row, col)
                                                } else {
                                                    selectedRow = null
                                                    selectedCol = null
                                                    validTargetMoves = emptyList()
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Sleek Glowing Green Target Dot for Valid Move
                                    if (isValidTarget) {
                                        Box(
                                            modifier = Modifier
                                                .size(if (piece != null) 36.dp else 16.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (piece != null) Color(0x99EF4444) else EmeraldGreen.copy(alpha = 0.85f)
                                                )
                                                .border(
                                                    width = 2.dp,
                                                    color = PureWhite,
                                                    shape = CircleShape
                                                )
                                        )
                                    }

                                    // Chess Piece Text
                                    if (piece != null) {
                                        Text(
                                            text = piece.symbol,
                                            fontSize = 32.sp,
                                            color = if (piece.isWhite) PureWhite else Color(0xFF1E293B),
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.shadow(2.dp, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons: Resign vs Checkmate Victory
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onGameFinished(false, 0L) },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("RESIGN 🏳️", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onGameFinished(true, stake.prizePool) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("CHECKMATE! 🏆", fontWeight = FontWeight.Black)
            }
        }
    }
}
