package com.example.oddevenduel.domain.model

enum class GameResult {
    PLAYER_WIN,
    CPU_WIN,
    DRAW,
    PERFECT_DUEL;

    fun title(): String = when (this) {
        PLAYER_WIN -> "YOU WIN"
        CPU_WIN -> "CPU WINS"
        DRAW -> "DRAW"
        PERFECT_DUEL -> "PERFECT DUEL"
    }

    fun description(): String = when (this) {
        PLAYER_WIN -> "You claimed victory with a superior score!"
        CPU_WIN -> "The CPU outplayed you this round."
        DRAW -> "Both players finished evenly."
        PERFECT_DUEL -> "Every choice was correct."
    }
}
