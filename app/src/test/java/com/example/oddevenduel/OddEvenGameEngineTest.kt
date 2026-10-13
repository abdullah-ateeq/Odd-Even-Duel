package com.example.oddevenduel

import com.example.oddevenduel.domain.logic.OddEvenGameEngine
import com.example.oddevenduel.domain.model.CellState
import com.example.oddevenduel.domain.model.Coordinate
import com.example.oddevenduel.domain.model.GamePhase
import com.example.oddevenduel.domain.model.GameResult
import com.example.oddevenduel.domain.model.NumberCell
import com.example.oddevenduel.domain.model.OddEvenGameState
import com.example.oddevenduel.domain.model.ParityChallenge
import com.example.oddevenduel.domain.model.Side
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OddEvenGameEngineTest {

    // 18. Player correct selection & TEST CASE — ODD
    @Test
    fun testPlayerCorrectOddSelection() {
        val cell17 = NumberCell(0, Coordinate(0, 0), 17, CellState.ACTIVE)
        val cell24 = NumberCell(1, Coordinate(0, 1), 24, CellState.ACTIVE)
        val state = OddEvenGameState(
            cells = listOf(cell17, cell24),
            phase = GamePhase.PLAYER_TURN,
            challenge = ParityChallenge.ODD,
            activeCells = 2,
            totalCells = 2
        )

        val nextState = OddEvenGameEngine.processPlayerSelection(state, cellId = 0)

        // Expected: 17 % 2 != 0, correct. Tile captured.
        assertTrue(nextState.lastSelectedCorrect == true)
        assertEquals(1, nextState.playerCells)
        assertEquals(11, nextState.playerScore) // 10 + (17 / 10) = 11
        assertEquals(1, nextState.activeCells)
        assertEquals(CellState.PLAYER_CLAIMED, nextState.cells.first { it.id == 0 }.state)
        assertEquals(GamePhase.CPU_TURN, nextState.phase)
    }

    // TEST CASE — EVEN
    @Test
    fun testPlayerCorrectEvenSelection() {
        val cell24 = NumberCell(1, Coordinate(0, 1), 24, CellState.ACTIVE)
        val cell17 = NumberCell(0, Coordinate(0, 0), 17, CellState.ACTIVE)
        val state = OddEvenGameState(
            cells = listOf(cell17, cell24),
            phase = GamePhase.PLAYER_TURN,
            challenge = ParityChallenge.EVEN,
            activeCells = 2,
            totalCells = 2
        )

        val nextState = OddEvenGameEngine.processPlayerSelection(state, cellId = 1)

        // Expected: 24 % 2 == 0, correct. Tile captured.
        assertTrue(nextState.lastSelectedCorrect == true)
        assertEquals(1, nextState.playerCells)
        assertEquals(12, nextState.playerScore) // 10 + (24 / 10) = 12
        assertEquals(CellState.PLAYER_CLAIMED, nextState.cells.first { it.id == 1 }.state)
        assertEquals(GamePhase.CPU_TURN, nextState.phase)
    }

    // 19. Player incorrect selection & TEST CASE — WRONG PARITY
    @Test
    fun testPlayerIncorrectSelection() {
        val cell17 = NumberCell(0, Coordinate(0, 0), 17, CellState.ACTIVE)
        val cell24 = NumberCell(1, Coordinate(0, 1), 24, CellState.ACTIVE)
        val state = OddEvenGameState(
            cells = listOf(cell17, cell24),
            phase = GamePhase.PLAYER_TURN,
            challenge = ParityChallenge.ODD,
            activeCells = 2,
            totalCells = 2
        )

        // Player selects 24 when challenge is ODD
        val nextState = OddEvenGameEngine.processPlayerSelection(state, cellId = 1)

        // Expected: Incorrect. Tile remains active. No score. Turn passes to CPU.
        assertEquals(false, nextState.lastSelectedCorrect)
        assertEquals(0, nextState.playerScore)
        assertEquals(0, nextState.playerCells)
        assertEquals(1, nextState.playerIncorrectSelections)
        assertEquals(CellState.ACTIVE, nextState.cells.first { it.id == 1 }.state)
        assertEquals(2, nextState.activeCells)
        assertEquals(GamePhase.CPU_TURN, nextState.phase)
    }

    // 22. Captured-cell protection & TEST CASE — CAPTURED CELL
    @Test
    fun testCapturedCellProtection() {
        val cell17 = NumberCell(0, Coordinate(0, 0), 17, CellState.PLAYER_CLAIMED)
        val cell24 = NumberCell(1, Coordinate(0, 1), 24, CellState.ACTIVE)
        val state = OddEvenGameState(
            cells = listOf(cell17, cell24),
            phase = GamePhase.PLAYER_TURN,
            challenge = ParityChallenge.ODD,
            playerScore = 11,
            playerCells = 1
        )

        // Player taps already captured cell 17
        val nextState = OddEvenGameEngine.processPlayerSelection(state, cellId = 0)

        // Expected: Ignore. No score change. No turn change.
        assertEquals(state, nextState)
        assertEquals(GamePhase.PLAYER_TURN, nextState.phase)
        assertEquals(11, nextState.playerScore)
    }

    // 20. CPU correct selection & 21. CPU incorrect selection
    @Test
    fun testCpuSelections() {
        val cell10 = NumberCell(0, Coordinate(0, 0), 10, CellState.ACTIVE)
        val cell15 = NumberCell(1, Coordinate(0, 1), 15, CellState.ACTIVE)
        val state = OddEvenGameState(
            cells = listOf(cell10, cell15),
            phase = GamePhase.CPU_TURN,
            challenge = ParityChallenge.EVEN,
            activeCells = 2
        )

        // CPU picks 10 (even)
        val correctState = OddEvenGameEngine.processCpuSelection(state, cell10)
        assertTrue(correctState.lastSelectedCorrect == true)
        assertEquals(1, correctState.cpuCells)
        assertEquals(11, correctState.cpuScore) // 10 + 1 = 11
        assertEquals(Side.CPU, correctState.lastClaimedBy)
        assertEquals(GamePhase.PLAYER_TURN, correctState.phase)

        // CPU picks 15 (odd)
        val incorrectState = OddEvenGameEngine.processCpuSelection(state, cell15)
        assertEquals(false, incorrectState.lastSelectedCorrect)
        assertEquals(0, incorrectState.cpuCells)
        assertEquals(0, incorrectState.cpuScore)
        assertEquals(1, incorrectState.cpuIncorrectSelections)
        assertEquals(GamePhase.PLAYER_TURN, incorrectState.phase)
    }

    // 45. Timeout
    @Test
    fun testTimeoutHandling() {
        val cell10 = NumberCell(0, Coordinate(0, 0), 10, CellState.ACTIVE)
        val state = OddEvenGameState(
            cells = listOf(cell10),
            phase = GamePhase.PLAYER_TURN,
            challenge = ParityChallenge.EVEN,
            playerTimeouts = 0
        )

        val afterTimeout = OddEvenGameEngine.processTimeout(state)
        assertEquals(1, afterTimeout.playerTimeouts)
        assertEquals(GamePhase.CPU_TURN, afterTimeout.phase)
        assertEquals("TIME'S UP", afterTimeout.lastFeedbackText)
    }

    // 37. Game completion & TEST CASE — GAME END
    @Test
    fun testGameCompletionWhenLastCellCaptured() {
        val lastCell = NumberCell(0, Coordinate(0, 0), 30, CellState.ACTIVE)
        val state = OddEvenGameState(
            cells = listOf(lastCell),
            phase = GamePhase.PLAYER_TURN,
            challenge = ParityChallenge.EVEN,
            activeCells = 1,
            playerScore = 50,
            cpuScore = 40,
            playerIncorrectSelections = 1 // Has a prior mistake -> standard PLAYER_WIN
        )

        val finishedState = OddEvenGameEngine.processPlayerSelection(state, cellId = 0)
        assertEquals(0, finishedState.activeCells)
        assertEquals(GamePhase.GAME_OVER, finishedState.phase)
        assertEquals(GameResult.PLAYER_WIN, finishedState.result)
    }

    @Test
    fun testGameCompletionPerfectDuel() {
        val lastCell = NumberCell(0, Coordinate(0, 0), 30, CellState.ACTIVE)
        val state = OddEvenGameState(
            cells = listOf(lastCell),
            phase = GamePhase.PLAYER_TURN,
            challenge = ParityChallenge.EVEN,
            activeCells = 1,
            playerScore = 50,
            cpuScore = 40,
            playerIncorrectSelections = 0 // 0 mistakes -> PERFECT_DUEL
        )

        val finishedState = OddEvenGameEngine.processPlayerSelection(state, cellId = 0)
        assertEquals(0, finishedState.activeCells)
        assertEquals(GamePhase.GAME_OVER, finishedState.phase)
        assertEquals(GameResult.PERFECT_DUEL, finishedState.result)
    }

    // 40. Score tiebreaker & TEST CASE — SCORE TIEBREAKER
    @Test
    fun testScoreTiebreaker() {
        // Player: score 100, cells 10. CPU: score 100, cells 9 -> PLAYER WIN
        val result = OddEvenGameEngine.calculateResult(
            playerScore = 100,
            cpuScore = 100,
            playerCells = 10,
            cpuCells = 9,
            playerIncorrectSelections = 1,
            playerTimeouts = 0,
            playerCorrectSelections = 10
        )
        assertEquals(GameResult.PLAYER_WIN, result)

        // CPU more cells with equal score -> CPU WIN
        val cpuWinTiebreak = OddEvenGameEngine.calculateResult(
            playerScore = 100,
            cpuScore = 100,
            playerCells = 9,
            cpuCells = 10,
            playerIncorrectSelections = 1,
            playerTimeouts = 0,
            playerCorrectSelections = 9
        )
        assertEquals(GameResult.CPU_WIN, cpuWinTiebreak)
    }

    // 42. Draw & TEST CASE — DRAW
    @Test
    fun testDrawResult() {
        // Player: score 100, cells 10. CPU: score 100, cells 10 -> DRAW
        val result = OddEvenGameEngine.calculateResult(
            playerScore = 100,
            cpuScore = 100,
            playerCells = 10,
            cpuCells = 10,
            playerIncorrectSelections = 0,
            playerTimeouts = 0,
            playerCorrectSelections = 10
        )
        assertEquals(GameResult.DRAW, result)
    }

    // 43. Perfect duel & TEST CASE — PERFECT DUEL
    @Test
    fun testPerfectDuelResult() {
        // Player: correct selections > 0, wrong selections = 0, timeouts = 0, player score > CPU score
        val result = OddEvenGameEngine.calculateResult(
            playerScore = 150,
            cpuScore = 120,
            playerCells = 12,
            cpuCells = 10,
            playerIncorrectSelections = 0,
            playerTimeouts = 0,
            playerCorrectSelections = 12
        )
        assertEquals(GameResult.PERFECT_DUEL, result)
    }

    // 44. Accuracy
    @Test
    fun testAccuracyCalculations() {
        val initial = OddEvenGameState(playerSelections = 0, playerCorrectSelections = 0)
        assertEquals(100, initial.playerAccuracy)

        val half = OddEvenGameState(playerSelections = 10, playerCorrectSelections = 5)
        assertEquals(50, half.playerAccuracy)

        val perfect = OddEvenGameState(playerSelections = 8, playerCorrectSelections = 8)
        assertEquals(100, perfect.playerAccuracy)
    }
}
