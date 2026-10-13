package com.example.oddevenduel.domain.model

data class GameSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val cpuDifficulty: CpuDifficulty = CpuDifficulty.NORMAL,
    val turnTimer: TurnTimerOption = TurnTimerOption.OFF,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val showMathFeedback: Boolean = true,
    val confirmRestart: Boolean = true,
    val showCoordinates: Boolean = false
)
