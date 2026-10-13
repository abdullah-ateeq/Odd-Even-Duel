package com.example.oddevenduel.domain.model

enum class CpuDifficulty(
    val displayName: String,
    val mistakeRatePercent: Int,
    val description: String
) {
    EASY("Easy", 35, "Frequently misidentifies parity and selects randomly."),
    NORMAL("Normal", 10, "Balanced competitor that occasionally misses."),
    HARD("Hard", 2, "Calculated master that targets high-value tiles.")
}
