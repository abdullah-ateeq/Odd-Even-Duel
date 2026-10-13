package com.example.oddevenduel

import com.example.oddevenduel.domain.logic.OddEvenBoardGenerator
import com.example.oddevenduel.domain.logic.ParityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class OddEvenBoardGeneratorTest {

    // 11. Board generation & 12. Exactly 36 cells
    @Test
    fun testBoardGenerationCellCount() {
        val board = OddEvenBoardGenerator.generateBoard()
        assertEquals(36, board.size)
        assertEquals(OddEvenBoardGenerator.TOTAL_CELLS, board.size)
    }

    // 13. Number range 1-50
    @Test
    fun testBoardNumberRange() {
        val board = OddEvenBoardGenerator.generateBoard()
        for (cell in board) {
            assertTrue("Value ${cell.value} must be >= 1", cell.value >= 1)
            assertTrue("Value ${cell.value} must be <= 50", cell.value <= 50)
        }
    }

    // 14. Unique IDs
    @Test
    fun testBoardUniqueIds() {
        val board = OddEvenBoardGenerator.generateBoard()
        val ids = board.map { it.id }.toSet()
        assertEquals(36, ids.size)
    }

    // 15. Unique coordinates
    @Test
    fun testBoardUniqueCoordinates() {
        val board = OddEvenBoardGenerator.generateBoard()
        val coords = board.map { it.coordinate }.toSet()
        assertEquals(36, coords.size)

        for (cell in board) {
            assertTrue(cell.coordinate.row in 0 until 6)
            assertTrue(cell.coordinate.column in 0 until 6)
        }
    }

    // 16. Odd/even distribution
    @Test
    fun testBoardParityDistribution() {
        val board = OddEvenBoardGenerator.generateBoard()
        val oddCount = board.count { ParityEngine.isOdd(it.value) }
        val evenCount = board.count { ParityEngine.isEven(it.value) }

        assertEquals(36, oddCount + evenCount)
        assertTrue("Odd count $oddCount must be between 15 and 21", oddCount in 15..21)
        assertTrue("Even count $evenCount must be between 15 and 21", evenCount in 15..21)
    }

    // 52. Random board validity across 50 generations
    @Test
    fun testRandomBoardValidityMultiRun() {
        val rand = Random(42)
        repeat(50) {
            val board = OddEvenBoardGenerator.generateBoard(rand)
            assertTrue(OddEvenBoardGenerator.validateBoard(board))
        }
    }
}
