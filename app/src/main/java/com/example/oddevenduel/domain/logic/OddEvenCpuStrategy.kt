package com.example.oddevenduel.domain.logic

import com.example.oddevenduel.domain.model.CpuDifficulty
import com.example.oddevenduel.domain.model.NumberCell
import com.example.oddevenduel.domain.model.ParityChallenge
import kotlin.random.Random

object OddEvenCpuStrategy {

    fun findValidCells(
        cells: List<NumberCell>,
        challenge: ParityChallenge
    ): List<NumberCell> {
        return cells.filter { it.isActive && ParityEngine.matchesChallenge(it.value, challenge) }
    }

    fun findInvalidCells(
        cells: List<NumberCell>,
        challenge: ParityChallenge
    ): List<NumberCell> {
        return cells.filter { it.isActive && !ParityEngine.matchesChallenge(it.value, challenge) }
    }

    fun selectCell(
        cells: List<NumberCell>,
        challenge: ParityChallenge,
        difficulty: CpuDifficulty,
        random: Random = Random
    ): NumberCell? {
        val activeCells = cells.filter { it.isActive }
        if (activeCells.isEmpty()) return null

        val validCells = findValidCells(cells, challenge)
        val invalidCells = findInvalidCells(cells, challenge)

        // Determine if CPU makes an intentional mistake based on difficulty mistake rate
        val mistakeRoll = random.nextInt(100) // 0..99
        val shouldMakeMistake = mistakeRoll < difficulty.mistakeRatePercent && invalidCells.isNotEmpty()

        if (shouldMakeMistake) {
            // Pick an invalid cell to register a miss
            return invalidCells[random.nextInt(invalidCells.size)]
        }

        // If no valid cells exist (should not happen if challenge generator is used), pick any active cell
        if (validCells.isEmpty()) {
            return activeCells[random.nextInt(activeCells.size)]
        }

        return when (difficulty) {
            CpuDifficulty.EASY -> {
                // Easy: Just picks a random valid cell (mistakes already handled above)
                validCells[random.nextInt(validCells.size)]
            }
            CpuDifficulty.NORMAL -> {
                // Normal: 50% picks the highest score valid cell, 50% random valid cell
                if (random.nextBoolean()) {
                    val maxScore = validCells.maxOf { ParityEngine.calculateTileScore(it.value) }
                    val highest = validCells.filter { ParityEngine.calculateTileScore(it.value) == maxScore }
                    highest[random.nextInt(highest.size)]
                } else {
                    validCells[random.nextInt(validCells.size)]
                }
            }
            CpuDifficulty.HARD -> {
                // Hard: 90% picks the absolute highest score valid cell, 10% second highest or random valid
                val sorted = validCells.sortedByDescending { ParityEngine.calculateTileScore(it.value) }
                val maxScore = sorted.first().let { ParityEngine.calculateTileScore(it.value) }
                val topTier = validCells.filter { ParityEngine.calculateTileScore(it.value) == maxScore }
                if (random.nextInt(100) < 90) {
                    topTier[random.nextInt(topTier.size)]
                } else {
                    validCells[random.nextInt(validCells.size)]
                }
            }
        }
    }
}
