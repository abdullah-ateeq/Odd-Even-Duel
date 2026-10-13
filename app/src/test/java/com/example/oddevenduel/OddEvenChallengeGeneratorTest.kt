package com.example.oddevenduel

import com.example.oddevenduel.domain.logic.OddEvenBoardGenerator
import com.example.oddevenduel.domain.logic.OddEvenChallengeGenerator
import com.example.oddevenduel.domain.logic.ParityEngine
import com.example.oddevenduel.domain.model.CellState
import com.example.oddevenduel.domain.model.Coordinate
import com.example.oddevenduel.domain.model.NumberCell
import com.example.oddevenduel.domain.model.ParityChallenge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class OddEvenChallengeGeneratorTest {

    // 29. Challenge generation & 30. Challenge always solvable & 31. No impossible challenge
    @Test
    fun testChallengeAlwaysSolvableOnFreshBoard() {
        val board = OddEvenBoardGenerator.generateBoard()
        val challenge = OddEvenChallengeGenerator.generateNextChallenge(board)
        val validCells = board.filter { it.isActive && ParityEngine.matchesChallenge(it.value, challenge) }
        assertTrue("Challenge must have valid active targets", validCells.isNotEmpty())
    }

    // TEST CASE — NO VALID ODD
    @Test
    fun testNoValidOddForcesEvenChallenge() {
        // Remaining active numbers: 2, 4, 6, 8 (all even)
        val cells = listOf(
            NumberCell(0, Coordinate(0, 0), 2, CellState.ACTIVE),
            NumberCell(1, Coordinate(0, 1), 4, CellState.ACTIVE),
            NumberCell(2, Coordinate(0, 2), 6, CellState.ACTIVE),
            NumberCell(3, Coordinate(0, 3), 8, CellState.ACTIVE),
            NumberCell(4, Coordinate(0, 4), 17, CellState.PLAYER_CLAIMED) // Odd but claimed
        )

        repeat(20) {
            val challenge = OddEvenChallengeGenerator.generateNextChallenge(cells, previousChallenge = ParityChallenge.EVEN)
            assertEquals("When no active odd cells exist, challenge must be EVEN", ParityChallenge.EVEN, challenge)
        }
    }

    // TEST CASE — NO VALID EVEN
    @Test
    fun testNoValidEvenForcesOddChallenge() {
        // Remaining active numbers: 1, 3, 5, 7 (all odd)
        val cells = listOf(
            NumberCell(0, Coordinate(0, 0), 1, CellState.ACTIVE),
            NumberCell(1, Coordinate(0, 1), 3, CellState.ACTIVE),
            NumberCell(2, Coordinate(0, 2), 5, CellState.ACTIVE),
            NumberCell(3, Coordinate(0, 3), 7, CellState.ACTIVE),
            NumberCell(4, Coordinate(0, 4), 20, CellState.CPU_CLAIMED) // Even but claimed
        )

        repeat(20) {
            val challenge = OddEvenChallengeGenerator.generateNextChallenge(cells, previousChallenge = ParityChallenge.ODD)
            assertEquals("When no active even cells exist, challenge must be ODD", ParityChallenge.ODD, challenge)
        }
    }

    @Test
    fun testRepetitiveChallengeMitigation() {
        val board = OddEvenBoardGenerator.generateBoard()
        val rand = Random(123)
        var switches = 0
        val iterations = 1000

        var current = ParityChallenge.ODD
        repeat(iterations) {
            val next = OddEvenChallengeGenerator.generateNextChallenge(board, previousChallenge = current, random = rand)
            if (next != current) switches++
            current = next
        }

        // Weighted random should favor switching (approx 70% switch rate)
        val switchRate = switches.toDouble() / iterations
        assertTrue("Switch rate ($switchRate) should be > 55% due to alternation weight", switchRate > 0.55)
    }
}
