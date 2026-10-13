package com.example.oddevenduel.domain.model

data class DuelStatistics(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val bestScore: Int = 0,
    val bestCells: Int = 0,
    val bestAccuracy: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val perfectDuels: Int = 0,
    val totalCorrectPicks: Int = 0,
    val totalWrongPicks: Int = 0,
    val totalTimeouts: Int = 0,
    val totalCellsCaptured: Int = 0
) {
    val overallAccuracy: Int
        get() {
            val total = totalCorrectPicks + totalWrongPicks
            return if (total == 0) 100 else ((totalCorrectPicks.toDouble() / total) * 100).toInt().coerceIn(0, 100)
        }
}
