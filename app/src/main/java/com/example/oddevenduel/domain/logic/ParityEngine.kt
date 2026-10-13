package com.example.oddevenduel.domain.logic

import com.example.oddevenduel.domain.model.NumberParity
import com.example.oddevenduel.domain.model.ParityChallenge

object ParityEngine {

    fun isOdd(number: Int): Boolean {
        return number % 2 != 0
    }

    fun isEven(number: Int): Boolean {
        return number % 2 == 0
    }

    fun getParity(number: Int): NumberParity {
        return if (number % 2 == 0) {
            NumberParity.EVEN
        } else {
            NumberParity.ODD
        }
    }

    fun matchesChallenge(number: Int, challenge: ParityChallenge): Boolean {
        return when (challenge) {
            ParityChallenge.ODD -> isOdd(number)
            ParityChallenge.EVEN -> isEven(number)
        }
    }

    fun calculateTileScore(number: Int): Int {
        return 10 + (number / 10)
    }

    fun getParityExplanation(number: Int): String {
        return if (isOdd(number)) "$number IS ODD" else "$number IS EVEN"
    }
}
