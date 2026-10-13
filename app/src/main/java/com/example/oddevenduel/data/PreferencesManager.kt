package com.example.oddevenduel.data

import android.content.Context
import android.content.SharedPreferences
import com.example.oddevenduel.domain.model.AppTheme
import com.example.oddevenduel.domain.model.CpuDifficulty
import com.example.oddevenduel.domain.model.DuelStatistics
import com.example.oddevenduel.domain.model.GameResult
import com.example.oddevenduel.domain.model.GameSettings
import com.example.oddevenduel.domain.model.TurnTimerOption

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadSettings(): GameSettings {
        val themeName = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        val difficultyName = prefs.getString(KEY_DIFFICULTY, CpuDifficulty.NORMAL.name) ?: CpuDifficulty.NORMAL.name
        val timerName = prefs.getString(KEY_TURN_TIMER, TurnTimerOption.OFF.name) ?: TurnTimerOption.OFF.name

        val theme = runCatching { AppTheme.valueOf(themeName) }.getOrDefault(AppTheme.SYSTEM)
        val difficulty = runCatching { CpuDifficulty.valueOf(difficultyName) }.getOrDefault(CpuDifficulty.NORMAL)
        val timer = runCatching { TurnTimerOption.valueOf(timerName) }.getOrDefault(TurnTimerOption.OFF)

        return GameSettings(
            theme = theme,
            dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true),
            cpuDifficulty = difficulty,
            turnTimer = timer,
            soundEnabled = prefs.getBoolean(KEY_SOUND, true),
            hapticsEnabled = prefs.getBoolean(KEY_HAPTICS, true),
            animationsEnabled = prefs.getBoolean(KEY_ANIMATIONS, true),
            showMathFeedback = prefs.getBoolean(KEY_SHOW_MATH_FEEDBACK, true),
            confirmRestart = prefs.getBoolean(KEY_CONFIRM_RESTART, true),
            showCoordinates = prefs.getBoolean(KEY_SHOW_COORDINATES, false)
        )
    }

    fun saveSettings(settings: GameSettings) {
        prefs.edit().apply {
            putString(KEY_THEME, settings.theme.name)
            putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            putString(KEY_DIFFICULTY, settings.cpuDifficulty.name)
            putString(KEY_TURN_TIMER, settings.turnTimer.name)
            putBoolean(KEY_SOUND, settings.soundEnabled)
            putBoolean(KEY_HAPTICS, settings.hapticsEnabled)
            putBoolean(KEY_ANIMATIONS, settings.animationsEnabled)
            putBoolean(KEY_SHOW_MATH_FEEDBACK, settings.showMathFeedback)
            putBoolean(KEY_CONFIRM_RESTART, settings.confirmRestart)
            putBoolean(KEY_SHOW_COORDINATES, settings.showCoordinates)
            apply()
        }
    }

    fun resetSettings(): GameSettings {
        prefs.edit().apply {
            remove(KEY_THEME)
            remove(KEY_DYNAMIC_COLOR)
            remove(KEY_DIFFICULTY)
            remove(KEY_TURN_TIMER)
            remove(KEY_SOUND)
            remove(KEY_HAPTICS)
            remove(KEY_ANIMATIONS)
            remove(KEY_SHOW_MATH_FEEDBACK)
            remove(KEY_CONFIRM_RESTART)
            remove(KEY_SHOW_COORDINATES)
            apply()
        }
        return GameSettings()
    }

    fun loadStatistics(): DuelStatistics {
        return DuelStatistics(
            gamesPlayed = prefs.getInt(KEY_GAMES_PLAYED, 0),
            wins = prefs.getInt(KEY_WINS, 0),
            losses = prefs.getInt(KEY_LOSSES, 0),
            draws = prefs.getInt(KEY_DRAWS, 0),
            bestScore = prefs.getInt(KEY_BEST_SCORE, 0),
            bestCells = prefs.getInt(KEY_BEST_CELLS, 0),
            bestAccuracy = prefs.getInt(KEY_BEST_ACCURACY, 0),
            currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0),
            bestStreak = prefs.getInt(KEY_BEST_STREAK, 0),
            perfectDuels = prefs.getInt(KEY_PERFECT_DUELS, 0),
            totalCorrectPicks = prefs.getInt(KEY_TOTAL_CORRECT_PICKS, 0),
            totalWrongPicks = prefs.getInt(KEY_TOTAL_WRONG_PICKS, 0),
            totalTimeouts = prefs.getInt(KEY_TOTAL_TIMEOUTS, 0),
            totalCellsCaptured = prefs.getInt(KEY_TOTAL_CELLS_CAPTURED, 0)
        )
    }

    fun recordGameResult(
        result: GameResult,
        playerScore: Int,
        playerCells: Int,
        playerAccuracy: Int,
        correctPicks: Int,
        wrongPicks: Int,
        timeouts: Int
    ): DuelStatistics {
        val current = loadStatistics()

        val isWin = result == GameResult.PLAYER_WIN || result == GameResult.PERFECT_DUEL
        val isLoss = result == GameResult.CPU_WIN
        val isDraw = result == GameResult.DRAW
        val isPerfect = result == GameResult.PERFECT_DUEL

        val newStreak = when {
            isWin -> current.currentStreak + 1
            isLoss -> 0
            else -> current.currentStreak
        }

        val newBestStreak = maxOf(current.bestStreak, newStreak)
        val newBestScore = maxOf(current.bestScore, playerScore)
        val newBestCells = maxOf(current.bestCells, playerCells)
        val newBestAccuracy = maxOf(current.bestAccuracy, playerAccuracy)

        val updated = current.copy(
            gamesPlayed = current.gamesPlayed + 1,
            wins = current.wins + (if (isWin) 1 else 0),
            losses = current.losses + (if (isLoss) 1 else 0),
            draws = current.draws + (if (isDraw) 1 else 0),
            bestScore = newBestScore,
            bestCells = newBestCells,
            bestAccuracy = newBestAccuracy,
            currentStreak = newStreak,
            bestStreak = newBestStreak,
            perfectDuels = current.perfectDuels + (if (isPerfect) 1 else 0),
            totalCorrectPicks = current.totalCorrectPicks + correctPicks,
            totalWrongPicks = current.totalWrongPicks + wrongPicks,
            totalTimeouts = current.totalTimeouts + timeouts,
            totalCellsCaptured = current.totalCellsCaptured + playerCells
        )

        prefs.edit().apply {
            putInt(KEY_GAMES_PLAYED, updated.gamesPlayed)
            putInt(KEY_WINS, updated.wins)
            putInt(KEY_LOSSES, updated.losses)
            putInt(KEY_DRAWS, updated.draws)
            putInt(KEY_BEST_SCORE, updated.bestScore)
            putInt(KEY_BEST_CELLS, updated.bestCells)
            putInt(KEY_BEST_ACCURACY, updated.bestAccuracy)
            putInt(KEY_CURRENT_STREAK, updated.currentStreak)
            putInt(KEY_BEST_STREAK, updated.bestStreak)
            putInt(KEY_PERFECT_DUELS, updated.perfectDuels)
            putInt(KEY_TOTAL_CORRECT_PICKS, updated.totalCorrectPicks)
            putInt(KEY_TOTAL_WRONG_PICKS, updated.totalWrongPicks)
            putInt(KEY_TOTAL_TIMEOUTS, updated.totalTimeouts)
            putInt(KEY_TOTAL_CELLS_CAPTURED, updated.totalCellsCaptured)
            apply()
        }

        return updated
    }

    fun resetStatistics(): DuelStatistics {
        prefs.edit().apply {
            remove(KEY_GAMES_PLAYED)
            remove(KEY_WINS)
            remove(KEY_LOSSES)
            remove(KEY_DRAWS)
            remove(KEY_BEST_SCORE)
            remove(KEY_BEST_CELLS)
            remove(KEY_BEST_ACCURACY)
            remove(KEY_CURRENT_STREAK)
            remove(KEY_BEST_STREAK)
            remove(KEY_PERFECT_DUELS)
            remove(KEY_TOTAL_CORRECT_PICKS)
            remove(KEY_TOTAL_WRONG_PICKS)
            remove(KEY_TOTAL_TIMEOUTS)
            remove(KEY_TOTAL_CELLS_CAPTURED)
            apply()
        }
        return DuelStatistics()
    }

    companion object {
        private const val PREFS_NAME = "odd_even_duel_prefs"

        private const val KEY_THEME = "setting_theme"
        private const val KEY_DYNAMIC_COLOR = "setting_dynamic_color"
        private const val KEY_DIFFICULTY = "setting_difficulty"
        private const val KEY_TURN_TIMER = "setting_turn_timer"
        private const val KEY_SOUND = "setting_sound"
        private const val KEY_HAPTICS = "setting_haptics"
        private const val KEY_ANIMATIONS = "setting_animations"
        private const val KEY_SHOW_MATH_FEEDBACK = "setting_show_math_feedback"
        private const val KEY_CONFIRM_RESTART = "setting_confirm_restart"
        private const val KEY_SHOW_COORDINATES = "setting_show_coordinates"

        private const val KEY_GAMES_PLAYED = "stat_games_played"
        private const val KEY_WINS = "stat_wins"
        private const val KEY_LOSSES = "stat_losses"
        private const val KEY_DRAWS = "stat_draws"
        private const val KEY_BEST_SCORE = "stat_best_score"
        private const val KEY_BEST_CELLS = "stat_best_cells"
        private const val KEY_BEST_ACCURACY = "stat_best_accuracy"
        private const val KEY_CURRENT_STREAK = "stat_current_streak"
        private const val KEY_BEST_STREAK = "stat_best_streak"
        private const val KEY_PERFECT_DUELS = "stat_perfect_duels"
        private const val KEY_TOTAL_CORRECT_PICKS = "stat_total_correct"
        private const val KEY_TOTAL_WRONG_PICKS = "stat_total_wrong"
        private const val KEY_TOTAL_TIMEOUTS = "stat_total_timeouts"
        private const val KEY_TOTAL_CELLS_CAPTURED = "stat_total_cells_captured"
    }
}
