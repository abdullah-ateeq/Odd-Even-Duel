package com.example.oddevenduel.domain.logic

import com.example.oddevenduel.domain.model.NumberCell
import com.example.oddevenduel.domain.model.ParityChallenge
import kotlin.random.Random

object OddEvenChallengeGenerator {

    /**
     * Generates a ParityChallenge ensuring that at least one active cell exists that satisfies it.
     * When both odd and even active cells exist, applies a weighted random selection favoring the
     * alternate parity of [previousChallenge] to avoid long repetitive streaks.
     */
    fun generateNextChallenge(
        cells: List<NumberCell>,
        previousChallenge: ParityChallenge? = null,
        random: Random = Random
    ): ParityChallenge {
        val activeCells = cells.filter { it.isActive }
        val hasActiveOdd = activeCells.any { ParityEngine.isOdd(it.value) }
        val hasActiveEven = activeCells.any { ParityEngine.isEven(it.value) }

        if (!hasActiveOdd && !hasActiveEven) {
            // Fallback if all are claimed
            return previousChallenge ?: ParityChallenge.ODD
        }

        if (hasActiveOdd && !hasActiveEven) {
            return ParityChallenge.ODD
        }

        if (!hasActiveOdd && hasActiveEven) {
            return ParityChallenge.EVEN
        }

        // Both parities are available: use weighted random to avoid repetitive challenges
        return when (previousChallenge) {
            ParityChallenge.ODD -> {
                // 70% chance to switch to EVEN, 30% chance to repeat ODD
                if (random.nextDouble() < 0.70) ParityChallenge.EVEN else ParityChallenge.ODD
            }
            ParityChallenge.EVEN -> {
                // 70% chance to switch to ODD, 30% chance to repeat EVEN
                if (random.nextDouble() < 0.70) ParityChallenge.ODD else ParityChallenge.EVEN
            }
            null -> {
                // 50/50 initial choice
                if (random.nextBoolean()) ParityChallenge.ODD else ParityChallenge.EVEN
            }
        }
    }
}
