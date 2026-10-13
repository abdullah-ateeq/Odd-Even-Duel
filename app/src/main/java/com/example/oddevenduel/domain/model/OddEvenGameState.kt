package com.example.oddevenduel.domain.model

data class OddEvenGameState(
    val cells: List<NumberCell> = emptyList(),
    val phase: GamePhase = GamePhase.PLAYER_TURN,
    val challenge: ParityChallenge = ParityChallenge.ODD,
    val playerCells: Int = 0,
    val cpuCells: Int = 0,
    val playerScore: Int = 0,
    val cpuScore: Int = 0,
    val activeCells: Int = 36,
    val totalCells: Int = 36,
    val playerSelections: Int = 0,
    val playerCorrectSelections: Int = 0,
    val playerIncorrectSelections: Int = 0,
    val playerTimeouts: Int = 0,
    val cpuSelections: Int = 0,
    val cpuCorrectSelections: Int = 0,
    val cpuIncorrectSelections: Int = 0,
    val lastSelectedValue: Int? = null,
    val lastSelectedCorrect: Boolean? = null,
    val lastClaimedBy: Side? = null,
    val result: GameResult? = null,
    val lastFeedbackText: String? = null,
    val lastFeedbackSubtext: String? = null,
    val turnTimeRemaining: Int? = null,
    val animatingCellId: Int? = null,
    val shakeCellId: Int? = null
) {
    val playerAccuracy: Int
        get() = if (playerSelections == 0) {
            100
        } else {
            ((playerCorrectSelections.toDouble() / playerSelections) * 100).toInt().coerceIn(0, 100)
        }

    val cpuAccuracy: Int
        get() = if (cpuSelections == 0) {
            100
        } else {
            ((cpuCorrectSelections.toDouble() / cpuSelections) * 100).toInt().coerceIn(0, 100)
        }
}
