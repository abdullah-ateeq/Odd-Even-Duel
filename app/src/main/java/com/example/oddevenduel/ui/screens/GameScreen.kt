package com.example.oddevenduel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oddevenduel.domain.logic.OddEvenBoardGenerator
import com.example.oddevenduel.domain.model.GamePhase
import com.example.oddevenduel.ui.OddEvenViewModel
import com.example.oddevenduel.ui.components.BoardCellView
import com.example.oddevenduel.ui.components.ChallengeCard
import com.example.oddevenduel.ui.components.ScoreboardCard

@Composable
fun GameScreen(
    viewModel: OddEvenViewModel,
    onNavigateBack: () -> Unit,
    onNavigateResult: () -> Unit
) {
    val state by viewModel.gameState.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showRestartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.phase) {
        if (state.phase == GamePhase.GAME_OVER) {
            onNavigateResult()
        }
    }

    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text(text = "Restart Duel?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Are you sure you want to restart this duel? Current board progress will be reset.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestartDialog = false
                        viewModel.startNewGame()
                    }
                ) {
                    Text(text = "Restart", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            // Landscape Layout: Board on left, controls & scoreboard on right
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Board (Left)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    BoardGrid(
                        cells = state.cells,
                        showCoordinates = settings.showCoordinates,
                        animationsEnabled = settings.animationsEnabled,
                        animatingCellId = state.animatingCellId,
                        shakeCellId = state.shakeCellId,
                        onCellClick = { cellId -> viewModel.selectCell(cellId) }
                    )
                }

                // Info & Controls (Right)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameHeader(
                        difficultyName = settings.cpuDifficulty.displayName,
                        onBack = onNavigateBack,
                        onRestart = {
                            if (settings.confirmRestart) {
                                showRestartDialog = true
                            } else {
                                viewModel.startNewGame()
                            }
                        }
                    )

                    ScoreboardCard(
                        playerCells = state.playerCells,
                        playerScore = state.playerScore,
                        cpuCells = state.cpuCells,
                        cpuScore = state.cpuScore,
                        activeCells = state.activeCells
                    )

                    ChallengeCard(
                        challenge = state.challenge,
                        phase = state.phase,
                        timerOption = settings.turnTimer,
                        timeRemaining = state.turnTimeRemaining
                    )

                    TurnAndFeedbackSection(
                        phase = state.phase,
                        feedbackText = state.lastFeedbackText,
                        feedbackSubtext = state.lastFeedbackSubtext
                    )
                }
            }
        } else {
            // Portrait Layout: Standard vertical flow
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameHeader(
                    difficultyName = settings.cpuDifficulty.displayName,
                    onBack = onNavigateBack,
                    onRestart = {
                        if (settings.confirmRestart) {
                            showRestartDialog = true
                        } else {
                            viewModel.startNewGame()
                        }
                    }
                )

                ScoreboardCard(
                    playerCells = state.playerCells,
                    playerScore = state.playerScore,
                    cpuCells = state.cpuCells,
                    cpuScore = state.cpuScore,
                    activeCells = state.activeCells
                )

                ChallengeCard(
                    challenge = state.challenge,
                    phase = state.phase,
                    timerOption = settings.turnTimer,
                    timeRemaining = state.turnTimeRemaining
                )

                // 6x6 Board centered
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BoardGrid(
                        cells = state.cells,
                        showCoordinates = settings.showCoordinates,
                        animationsEnabled = settings.animationsEnabled,
                        animatingCellId = state.animatingCellId,
                        shakeCellId = state.shakeCellId,
                        onCellClick = { cellId -> viewModel.selectCell(cellId) }
                    )
                }

                TurnAndFeedbackSection(
                    phase = state.phase,
                    feedbackText = state.lastFeedbackText,
                    feedbackSubtext = state.lastFeedbackSubtext
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GameHeader(
    difficultyName: String,
    onBack: () -> Unit,
    onRestart: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Return to Home",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "CPU: $difficultyName",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onRestart) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Restart duel",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BoardGrid(
    cells: List<com.example.oddevenduel.domain.model.NumberCell>,
    showCoordinates: Boolean,
    animationsEnabled: Boolean,
    animatingCellId: Int?,
    shakeCellId: Int?,
    onCellClick: (Int) -> Unit
) {
    val gridSize = OddEvenBoardGenerator.GRID_SIZE

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (r in 0 until gridSize) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (c in 0 until gridSize) {
                    val index = r * gridSize + c
                    if (index < cells.size) {
                        val cell = cells[index]
                        BoardCellView(
                            cell = cell,
                            showCoordinates = showCoordinates,
                            animationsEnabled = animationsEnabled,
                            isAnimatingSuccess = cell.id == animatingCellId,
                            isAnimatingShake = cell.id == shakeCellId,
                            onClick = { onCellClick(cell.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TurnAndFeedbackSection(
    phase: GamePhase,
    feedbackText: String?,
    feedbackSubtext: String?
) {
    val turnDescription = when (phase) {
        GamePhase.PLAYER_TURN -> "YOUR TURN"
        GamePhase.CPU_TURN -> "CPU TURN — Choosing a tile..."
        GamePhase.RESOLVING -> "Processing move..."
        GamePhase.GAME_OVER -> "Game Completed"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Turn: $turnDescription" },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = turnDescription,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (phase == GamePhase.PLAYER_TURN) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )

            if (!feedbackText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = feedbackText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!feedbackSubtext.isNullOrBlank()) {
                        Text(
                            text = " · $feedbackSubtext",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
