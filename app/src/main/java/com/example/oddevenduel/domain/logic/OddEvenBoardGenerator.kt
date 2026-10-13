package com.example.oddevenduel.domain.logic

import com.example.oddevenduel.domain.model.CellState
import com.example.oddevenduel.domain.model.Coordinate
import com.example.oddevenduel.domain.model.NumberCell
import kotlin.random.Random

object OddEvenBoardGenerator {

    const val GRID_SIZE = 6
    const val TOTAL_CELLS = GRID_SIZE * GRID_SIZE
    const val MIN_ODD_COUNT = 15
    const val MAX_ODD_COUNT = 21
    const val MIN_NUMBER = 1
    const val MAX_NUMBER = 50

    private val oddPool: List<Int> = (MIN_NUMBER..MAX_NUMBER).filter { ParityEngine.isOdd(it) }
    private val evenPool: List<Int> = (MIN_NUMBER..MAX_NUMBER).filter { ParityEngine.isEven(it) }

    fun generateBoard(random: Random = Random): List<NumberCell> {
        val oddTargetCount = random.nextInt(MIN_ODD_COUNT, MAX_ODD_COUNT + 1)
        val evenTargetCount = TOTAL_CELLS - oddTargetCount

        val values = ArrayList<Int>(TOTAL_CELLS)

        // Generate odd numbers
        repeat(oddTargetCount) {
            values.add(oddPool[random.nextInt(oddPool.size)])
        }

        // Generate even numbers
        repeat(evenTargetCount) {
            values.add(evenPool[random.nextInt(evenPool.size)])
        }

        // Shuffle the values
        values.shuffle(random)

        val cells = ArrayList<NumberCell>(TOTAL_CELLS)
        var id = 0
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                val value = values[id]
                cells.add(
                    NumberCell(
                        id = id,
                        coordinate = Coordinate(row = r, column = c),
                        value = value,
                        state = CellState.ACTIVE
                    )
                )
                id++
            }
        }

        return cells
    }

    fun validateBoard(cells: List<NumberCell>): Boolean {
        if (cells.size != TOTAL_CELLS) return false

        val ids = HashSet<Int>()
        val coordinates = HashSet<Coordinate>()
        var oddCount = 0
        var evenCount = 0

        for (cell in cells) {
            if (cell.value !in MIN_NUMBER..MAX_NUMBER) return false
            if (!ids.add(cell.id)) return false
            if (!coordinates.add(cell.coordinate)) return false
            if (cell.coordinate.row !in 0 until GRID_SIZE || cell.coordinate.column !in 0 until GRID_SIZE) return false

            if (ParityEngine.isOdd(cell.value)) {
                oddCount++
            } else {
                evenCount++
            }
        }

        if (oddCount == 0 || evenCount == 0) return false
        if (oddCount !in MIN_ODD_COUNT..MAX_ODD_COUNT) return false
        if (evenCount !in (TOTAL_CELLS - MAX_ODD_COUNT)..(TOTAL_CELLS - MIN_ODD_COUNT)) return false

        return true
    }
}
