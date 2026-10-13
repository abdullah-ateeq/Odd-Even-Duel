package com.example.oddevenduel.domain.logic

import com.example.oddevenduel.domain.model.CellState
import com.example.oddevenduel.domain.model.GamePhase
import com.example.oddevenduel.domain.model.GameResult
import com.example.oddevenduel.domain.model.NumberCell
import com.example.oddevenduel.domain.model.OddEvenGameState
import com.example.oddevenduel.domain.model.ParityChallenge
import com.example.oddevenduel.domain.model.Side
import kotlin.random.Random

object OddEvenGameEngine {

    fun startNewGame(random: Random = Random): OddEvenGameState {
        val board = OddEvenBoardGenerator.generateBoard(random)
        val initialChallenge = OddEvenChallengeGenerator.generateNextChallenge(
            cells = board,
            previousChallenge = null,
            random = random
        )
        return OddEvenGameState(
            cells = board,
            phase = GamePhase.PLAYER_TURN,
            challenge = initialChallenge,
            playerCells = 0,
            cpuCells = 0,
            playerScore = 0,
            cpuScore = 0,
            activeCells = board.size,
            totalCells = board.size,
            playerSelections = 0,
            playerCorrectSelections = 0,
            playerIncorrectSelections = 0,
            playerTimeouts = 0,
            cpuSelections = 0,
            cpuCorrectSelections = 0,
            cpuIncorrectSelections = 0,
            lastSelectedValue = null,
            lastSelectedCorrect = null,
            lastClaimedBy = null,
            result = null,
            lastFeedbackText = null,
            lastFeedbackSubtext = null,
            turnTimeRemaining = null,
            animatingCellId = null,
            shakeCellId = null
        )
    }

    fun processPlayerSelection(
        currentState: OddEvenGameState,
        cellId: Int,
        showMathFeedback: Boolean = true,
        random: Random = Random
    ): OddEvenGameState {
        if (currentState.phase != GamePhase.PLAYER_TURN) return currentState

        val cell = currentState.cells.find { it.id == cellId } ?: return currentState
        if (!cell.isActive) return currentState

        val isMatch = ParityEngine.matchesChallenge(cell.value, currentState.challenge)
        val newPlayerSelections = currentState.playerSelections + 1

        return if (isMatch) {
            val scoreGained = ParityEngine.calculateTileScore(cell.value)
            val updatedCells = currentState.cells.map {
                if (it.id == cellId) it.copy(state = CellState.PLAYER_CLAIMED) else it
            }
            val newPlayerCells = currentState.playerCells + 1
            val newPlayerScore = currentState.playerScore + scoreGained
            val newCorrectSelections = currentState.playerCorrectSelections + 1
            val newActiveCells = currentState.activeCells - 1

            val feedbackText = "CORRECT"
            val feedbackSubtext = if (showMathFeedback) {
                "${ParityEngine.getParityExplanation(cell.value)} · +$scoreGained"
            } else {
                "+$scoreGained"
            }

            if (newActiveCells == 0) {
                val finalResult = calculateResult(
                    playerScore = newPlayerScore,
                    cpuScore = currentState.cpuScore,
                    playerCells = newPlayerCells,
                    cpuCells = currentState.cpuCells,
                    playerIncorrectSelections = currentState.playerIncorrectSelections,
                    playerTimeouts = currentState.playerTimeouts,
                    playerCorrectSelections = newCorrectSelections
                )
                currentState.copy(
                    cells = updatedCells,
                    phase = GamePhase.GAME_OVER,
                    playerCells = newPlayerCells,
                    playerScore = newPlayerScore,
                    activeCells = 0,
                    playerSelections = newPlayerSelections,
                    playerCorrectSelections = newCorrectSelections,
                    lastSelectedValue = cell.value,
                    lastSelectedCorrect = true,
                    lastClaimedBy = Side.PLAYER,
                    result = finalResult,
                    lastFeedbackText = feedbackText,
                    lastFeedbackSubtext = feedbackSubtext,
                    animatingCellId = cellId,
                    shakeCellId = null
                )
            } else {
                val nextChallenge = OddEvenChallengeGenerator.generateNextChallenge(
                    cells = updatedCells,
                    previousChallenge = currentState.challenge,
                    random = random
                )
                currentState.copy(
                    cells = updatedCells,
                    phase = GamePhase.CPU_TURN,
                    challenge = nextChallenge,
                    playerCells = newPlayerCells,
                    playerScore = newPlayerScore,
                    activeCells = newActiveCells,
                    playerSelections = newPlayerSelections,
                    playerCorrectSelections = newCorrectSelections,
                    lastSelectedValue = cell.value,
                    lastSelectedCorrect = true,
                    lastClaimedBy = Side.PLAYER,
                    lastFeedbackText = feedbackText,
                    lastFeedbackSubtext = feedbackSubtext,
                    animatingCellId = cellId,
                    shakeCellId = null
                )
            }
        } else {
            val newIncorrectSelections = currentState.playerIncorrectSelections + 1
            val feedbackText = "WRONG PARITY"
            val feedbackSubtext = if (showMathFeedback) {
                "${ParityEngine.getParityExplanation(cell.value)} (Challenge: ${currentState.challenge.displayName()})"
            } else {
                "Challenge was ${currentState.challenge.displayName()}"
            }

            val nextChallenge = OddEvenChallengeGenerator.generateNextChallenge(
                cells = currentState.cells,
                previousChallenge = currentState.challenge,
                random = random
            )

            currentState.copy(
                phase = GamePhase.CPU_TURN,
                challenge = nextChallenge,
                playerSelections = newPlayerSelections,
                playerIncorrectSelections = newIncorrectSelections,
                lastSelectedValue = cell.value,
                lastSelectedCorrect = false,
                lastClaimedBy = null,
                lastFeedbackText = feedbackText,
                lastFeedbackSubtext = feedbackSubtext,
                animatingCellId = null,
                shakeCellId = cellId
            )
        }
    }

    fun processCpuSelection(
        currentState: OddEvenGameState,
        selectedCell: NumberCell,
        showMathFeedback: Boolean = true,
        random: Random = Random
    ): OddEvenGameState {
        if (currentState.phase != GamePhase.CPU_TURN) return currentState
        if (!selectedCell.isActive) return currentState

        val isMatch = ParityEngine.matchesChallenge(selectedCell.value, currentState.challenge)
        val newCpuSelections = currentState.cpuSelections + 1

        return if (isMatch) {
            val scoreGained = ParityEngine.calculateTileScore(selectedCell.value)
            val updatedCells = currentState.cells.map {
                if (it.id == selectedCell.id) it.copy(state = CellState.CPU_CLAIMED) else it
            }
            val newCpuCells = currentState.cpuCells + 1
            val newCpuScore = currentState.cpuScore + scoreGained
            val newCorrectSelections = currentState.cpuCorrectSelections + 1
            val newActiveCells = currentState.activeCells - 1

            val feedbackText = "CPU CLAIMED ${selectedCell.value}"
            val feedbackSubtext = "+$scoreGained"

            if (newActiveCells == 0) {
                val finalResult = calculateResult(
                    playerScore = currentState.playerScore,
                    cpuScore = newCpuScore,
                    playerCells = currentState.playerCells,
                    cpuCells = newCpuCells,
                    playerIncorrectSelections = currentState.playerIncorrectSelections,
                    playerTimeouts = currentState.playerTimeouts,
                    playerCorrectSelections = currentState.playerCorrectSelections
                )
                currentState.copy(
                    cells = updatedCells,
                    phase = GamePhase.GAME_OVER,
                    cpuCells = newCpuCells,
                    cpuScore = newCpuScore,
                    activeCells = 0,
                    cpuSelections = newCpuSelections,
                    cpuCorrectSelections = newCorrectSelections,
                    lastSelectedValue = selectedCell.value,
                    lastSelectedCorrect = true,
                    lastClaimedBy = Side.CPU,
                    result = finalResult,
                    lastFeedbackText = feedbackText,
                    lastFeedbackSubtext = feedbackSubtext,
                    animatingCellId = selectedCell.id,
                    shakeCellId = null
                )
            } else {
                val nextChallenge = OddEvenChallengeGenerator.generateNextChallenge(
                    cells = updatedCells,
                    previousChallenge = currentState.challenge,
                    random = random
                )
                currentState.copy(
                    cells = updatedCells,
                    phase = GamePhase.PLAYER_TURN,
                    challenge = nextChallenge,
                    cpuCells = newCpuCells,
                    cpuScore = newCpuScore,
                    activeCells = newActiveCells,
                    cpuSelections = newCpuSelections,
                    cpuCorrectSelections = newCorrectSelections,
                    lastSelectedValue = selectedCell.value,
                    lastSelectedCorrect = true,
                    lastClaimedBy = Side.CPU,
                    lastFeedbackText = feedbackText,
                    lastFeedbackSubtext = feedbackSubtext,
                    animatingCellId = selectedCell.id,
                    shakeCellId = null
                )
            }
        } else {
            val newIncorrectSelections = currentState.cpuIncorrectSelections + 1
            val feedbackText = "CPU MISSED"
            val feedbackSubtext = if (showMathFeedback) {
                "${ParityEngine.getParityExplanation(selectedCell.value)} (Challenge: ${currentState.challenge.displayName()})"
            } else {
                "Challenge was ${currentState.challenge.displayName()}"
            }

            val nextChallenge = OddEvenChallengeGenerator.generateNextChallenge(
                cells = currentState.cells,
                previousChallenge = currentState.challenge,
                random = random
            )

            currentState.copy(
                phase = GamePhase.PLAYER_TURN,
                challenge = nextChallenge,
                cpuSelections = newCpuSelections,
                cpuIncorrectSelections = newIncorrectSelections,
                lastSelectedValue = selectedCell.value,
                lastSelectedCorrect = false,
                lastClaimedBy = null,
                lastFeedbackText = feedbackText,
                lastFeedbackSubtext = feedbackSubtext,
                animatingCellId = null,
                shakeCellId = selectedCell.id
            )
        }
    }

    fun processTimeout(
        currentState: OddEvenGameState,
        random: Random = Random
    ): OddEvenGameState {
        if (currentState.phase != GamePhase.PLAYER_TURN) return currentState

        val nextChallenge = OddEvenChallengeGenerator.generateNextChallenge(
            cells = currentState.cells,
            previousChallenge = currentState.challenge,
            random = random
        )

        return currentState.copy(
            phase = GamePhase.CPU_TURN,
            challenge = nextChallenge,
            playerTimeouts = currentState.playerTimeouts + 1,
            lastFeedbackText = "TIME'S UP",
            lastFeedbackSubtext = "Turn passed to CPU",
            lastSelectedValue = null,
            lastSelectedCorrect = null,
            lastClaimedBy = null,
            animatingCellId = null,
            shakeCellId = null
        )
    }

    fun calculateResult(
        playerScore: Int,
        cpuScore: Int,
        playerCells: Int,
        cpuCells: Int,
        playerIncorrectSelections: Int,
        playerTimeouts: Int,
        playerCorrectSelections: Int
    ): GameResult {
        return if (playerScore > cpuScore) {
            if (playerIncorrectSelections == 0 && playerTimeouts == 0 && playerCorrectSelections > 0) {
                GameResult.PERFECT_DUEL
            } else {
                GameResult.PLAYER_WIN
            }
        } else if (cpuScore > playerScore) {
            GameResult.CPU_WIN
        } else if (playerCells > cpuCells) {
            GameResult.PLAYER_WIN
        } else if (cpuCells > playerCells) {
            GameResult.CPU_WIN
        } else {
            GameResult.DRAW
        }
    }
}
