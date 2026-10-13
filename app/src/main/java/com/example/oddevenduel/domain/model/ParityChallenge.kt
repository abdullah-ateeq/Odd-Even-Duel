package com.example.oddevenduel.domain.model

enum class ParityChallenge {
    ODD,
    EVEN;

    fun displayName(): String = when (this) {
        ODD -> "ODD"
        EVEN -> "EVEN"
    }

    fun promptText(): String = when (this) {
        ODD -> "Choose an odd number"
        EVEN -> "Choose an even number"
    }
}
