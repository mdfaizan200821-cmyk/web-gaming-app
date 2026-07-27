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
import com.example.ui.theme.*

data class BlockShape(
    val id: Int,
    val matrix: List<List<Boolean>>,
    val color: Color
)

@Composable
fun BlockPuzzleGame(
    onBack: () -> Unit,
    onGameFinished: (isWin: Boolean, rewardCoins: Long) -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var linesClearedCount by remember { mutableStateOf(0) }
    var gameStatus by remember { mutableStateOf("Select shape below & tap grid cell to place blocks! Clear full lines! 🟩") }
    var selectedShapeIndex by remember { mutableStateOf<Int?>(null) }

    // 8x8 Grid Board State (null = empty, Color = filled block)
    var gridState by remember {
        mutableStateOf(Array(8) { Array<Color?>(8) { null } })
    }

    // Available Preset Shapes
    val presetShapes = remember {
        listOf(
            BlockShape(1, listOf(listOf(true, true), listOf(true, true)), GemBlue),            // 2x2 Square
            BlockShape(2, listOf(listOf(true, true, true)), GoldenAmber),                     // 1x3 Line
            BlockShape(3, listOf(listOf(true), listOf(true), listOf(true)), EmeraldGreen),     // 3x1 Vertical
            BlockShape(4, listOf(listOf(true, false), listOf(true, true)), RoyalPurple),        // L-Shape
            BlockShape(5, listOf(listOf(true, true)), GemCyan),                               // 1x2 Small Line
            BlockShape(6, listOf(listOf(true)), CrimsonRed)                                    // 1x1 Dot
        )
    }

    // 3 Available Shapes at bottom
    var availableShapes by remember {
        mutableStateOf(listOf(presetShapes[0], presetShapes[1], presetShapes[2]))
    }

    // Function to place shape on grid
    fun tryPlaceShape(startRow: Int, startCol: Int, shape: BlockShape): Boolean {
        val rows = shape.matrix.size
        val cols = shape.matrix[0].size

        // Bounds Check
        if (startRow + rows > 8 || startCol + cols > 8) return false

        // Collision Check
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (shape.matrix[r][c] && gridState[startRow + r][startCol + c] != null) {
                    return false
                }
            }
        }

        // Place Blocks
        val newGrid = gridState.map { it.clone() }.toTypedArray()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (shape.matrix[r][c]) {
                    newGrid[startRow + r][startCol + c] = shape.color
                }
            }
        }

        // Check Line Clears (Horizontal & Vertical)
        val rowsToClear = mutableListOf<Int>()
        val colsToClear = mutableListOf<Int>()

        for (r in 0 until 8) {
            if ((0 until 8).all { c -> newGrid[r][c] != null }) rowsToClear.add(r)
        }
        for (c in 0 until 8) {
            if ((0 until 8).all { r -> newGrid[r][c] != null }) colsToClear.add(c)
        }

        for (r in rowsToClear) {
            for (c in 0 until 8) newGrid[r][c] = null
        }
        for (c in colsToClear) {
            for (r in 0 until 8) newGrid[r][c] = null
        }

        val linesCleared = rowsToClear.size + colsToClear.size
        if (linesCleared > 0) {
            linesClearedCount += linesCleared
            score += linesCleared * 100
            gameStatus = "💥 $linesCleared LINES CLEARED! +${linesCleared * 100} PTS!"
        } else {
            score += 20
        }

        gridState = newGrid
        return true
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
                Text("BLOCK PUZZLE 🧩", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DarkCharcoal)
                Text("SCORE: $score PTS  |  LINES CLEARED: $linesClearedCount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GemBlue)
            }
            Box(modifier = Modifier.size(24.dp))
        }

        Text(gameStatus, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GemBlue)

        // 8x8 GRID BOARD
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PureWhite,
            border = BorderStroke(2.dp, LightBorder),
            shadowElevation = 4.dp,
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (r in 0 until 8) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (c in 0 until 8) {
                            val cellColor = gridState[r][c]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        cellColor ?: SurfaceCardVariant,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        selectedShapeIndex?.let { idx ->
                                            val shape = availableShapes.getOrNull(idx)
                                            if (shape != null) {
                                                val placed = tryPlaceShape(r, c, shape)
                                                if (placed) {
                                                    val newShapes = availableShapes.toMutableList()
                                                    newShapes.removeAt(idx)
                                                    if (newShapes.isEmpty()) {
                                                        // Refresh shapes
                                                        newShapes.addAll(presetShapes.shuffled().take(3))
                                                    }
                                                    availableShapes = newShapes
                                                    selectedShapeIndex = null

                                                    if (score >= 300) {
                                                        onGameFinished(true, 2000L)
                                                    }
                                                }
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }

        // 3 Available Shapes at bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            availableShapes.forEachIndexed { index, shape ->
                val isSelected = selectedShapeIndex == index
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PureWhite,
                    border = BorderStroke(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) GoldenAmber else LightBorder
                    ),
                    modifier = Modifier
                        .clickable { selectedShapeIndex = index }
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        shape.matrix.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                row.forEach { cell ->
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(
                                                if (cell) shape.color else Color.Transparent,
                                                RoundedCornerShape(3.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onGameFinished(true, 1500L) },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("CLAIM BLOCK PUZZLE VICTORY 🏆", fontWeight = FontWeight.Black)
        }
    }
}
