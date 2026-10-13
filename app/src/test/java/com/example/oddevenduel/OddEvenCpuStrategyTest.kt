package com.example.oddevenduel

import com.example.oddevenduel.domain.logic.OddEvenCpuStrategy
import com.example.oddevenduel.domain.logic.ParityEngine
import com.example.oddevenduel.domain.model.CellState
import com.example.oddevenduel.domain.model.Coordinate
import com.example.oddevenduel.domain.model.CpuDifficulty
import com.example.oddevenduel.domain.model.NumberCell
import com.example.oddevenduel.domain.model.ParityChallenge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class OddEvenCpuStrategyTest {

    // 17. Valid-cell detection
    @Test
    fun testFindValidCells() {
        val cells = listOf(
            NumberCell(0, Coordinate(0, 0), 17, CellState.ACTIVE),
            NumberCell(1, Coordinate(0, 1), 24, CellState.ACTIVE),
            NumberCell(2, Coordinate(0, 2), 31, CellState.ACTIVE),
            NumberCell(3, Coordinate(0, 3), 42, CellState.ACTIVE),
            NumberCell(4, Coordinate(0, 4), 48, CellState.PLAYER_CLAIMED) // Even but claimed
        )

        val validOdd = OddEvenCpuStrategy.findValidCells(cells, ParityChallenge.ODD)
        assertEquals(2, validOdd.size)
        assertTrue(validOdd.any { it.value == 17 })
        assertTrue(validOdd.any { it.value == 31 })

        val validEven = OddEvenCpuStrategy.findValidCells(cells, ParityChallenge.EVEN)
        assertEquals(2, validEven.size)
        assertTrue(validEven.any { it.value == 24 })
        assertTrue(validEven.any { it.value == 42 })
    }

    // 36. CPU cannot select captured cells
    @Test
    fun testCpuCannotSelectCapturedCells() {
        val cells = listOf(
            NumberCell(0, Coordinate(0, 0), 10, CellState.PLAYER_CLAIMED),
            NumberCell(1, Coordinate(0, 1), 20, CellState.CPU_CLAIMED),
            NumberCell(2, Coordinate(0, 2), 30, CellState.ACTIVE)
        )

        val choice = OddEvenCpuStrategy.selectCell(
            cells = cells,
            challenge = ParityChallenge.EVEN,
            difficulty = CpuDifficulty.HARD
        )

        assertNotNull(choice)
        assertEquals(30, choice?.value)
        assertEquals(CellState.ACTIVE, choice?.state)
    }

    // TEST CASE — CPU
    // Challenge: EVEN. Active cells: 17, 24, 31, 42. Hard CPU. Expected: CPU selects valid even tile, prefer 42.
    @Test
    fun testHardCpuPrefersHighestValue() {
        val cells = listOf(
            NumberCell(0, Coordinate(0, 0), 17, CellState.ACTIVE),
            NumberCell(1, Coordinate(0, 1), 24, CellState.ACTIVE),
            NumberCell(2, Coordinate(0, 2), 31, CellState.ACTIVE),
            NumberCell(3, Coordinate(0, 3), 42, CellState.ACTIVE)
        )

        val rand = Random(100)
        var count42 = 0
        var count24 = 0
        val runs = 500

        repeat(runs) {
            val choice = OddEvenCpuStrategy.selectCell(
                cells = cells,
                challenge = ParityChallenge.EVEN,
                difficulty = CpuDifficulty.HARD,
                random = rand
            )
            assertNotNull(choice)
            if (choice?.value == 42) count42++
            if (choice?.value == 24) count24++
        }

        // Hard AI prioritizes the highest score tile (42) with high frequency
        assertTrue("Hard CPU should heavily prefer highest value 42 (got $count42 / $runs)", count42 > count24 * 3)
    }

    // 35. CPU mistake rates
    @Test
    fun testCpuMistakeRates() {
        val cells = listOf(
            NumberCell(0, Coordinate(0, 0), 17, CellState.ACTIVE), // Odd
            NumberCell(1, Coordinate(0, 1), 24, CellState.ACTIVE), // Even
            NumberCell(2, Coordinate(0, 2), 31, CellState.ACTIVE), // Odd
            NumberCell(3, Coordinate(0, 3), 42, CellState.ACTIVE)  // Even
        )

        val rand = Random(42)
        val iterations = 2000

        // Test Easy mistake rate ~35%
        var easyMistakes = 0
        repeat(iterations) {
            val choice = OddEvenCpuStrategy.selectCell(cells, ParityChallenge.EVEN, CpuDifficulty.EASY, rand)
            if (!ParityEngine.matchesChallenge(choice!!.value, ParityChallenge.EVEN)) {
                easyMistakes++
            }
        }
        val easyRate = easyMistakes.toDouble() / iterations
        assertTrue("Easy mistake rate should be around 35% (got $easyRate)", easyRate in 0.28..0.42)

        // Test Hard mistake rate ~2%
        var hardMistakes = 0
        repeat(iterations) {
            val choice = OddEvenCpuStrategy.selectCell(cells, ParityChallenge.EVEN, CpuDifficulty.HARD, rand)
            if (!ParityEngine.matchesChallenge(choice!!.value, ParityChallenge.EVEN)) {
                hardMistakes++
            }
        }
        val hardRate = hardMistakes.toDouble() / iterations
        assertTrue("Hard mistake rate should be around 2% (got $hardRate)", hardRate in 0.00..0.05)
    }
}
