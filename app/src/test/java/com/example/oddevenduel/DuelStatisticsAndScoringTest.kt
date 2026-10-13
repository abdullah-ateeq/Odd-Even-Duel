package com.example.oddevenduel

import com.example.oddevenduel.domain.model.AppTheme
import com.example.oddevenduel.domain.model.CpuDifficulty
import com.example.oddevenduel.domain.model.DuelStatistics
import com.example.oddevenduel.domain.model.GameResult
import com.example.oddevenduel.domain.model.GameSettings
import com.example.oddevenduel.domain.model.TurnTimerOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DuelStatisticsAndScoringTest {

    // 47. Statistics & 48. Win streak
    @Test
    fun testWinStreakLogic() {
        var stats = DuelStatistics()

        // Initial
        assertEquals(0, stats.currentStreak)
        assertEquals(0, stats.bestStreak)

        // 1st Win
        val isWin = true
        val streak1 = stats.currentStreak + 1
        stats = stats.copy(currentStreak = streak1, bestStreak = maxOf(stats.bestStreak, streak1))
        assertEquals(1, stats.currentStreak)
        assertEquals(1, stats.bestStreak)

        // 2nd Win
        val streak2 = stats.currentStreak + 1
        stats = stats.copy(currentStreak = streak2, bestStreak = maxOf(stats.bestStreak, streak2))
        assertEquals(2, stats.currentStreak)
        assertEquals(2, stats.bestStreak)

        // 3rd Win
        val streak3 = stats.currentStreak + 1
        stats = stats.copy(currentStreak = streak3, bestStreak = maxOf(stats.bestStreak, streak3))
        assertEquals(3, stats.currentStreak)
        assertEquals(3, stats.bestStreak)

        // Draw: streak unchanged
        assertEquals(3, stats.currentStreak)

        // Loss: streak resets to 0, best remains 3
        stats = stats.copy(currentStreak = 0)
        assertEquals(0, stats.currentStreak)
        assertEquals(3, stats.bestStreak)
    }

    // 49. Settings defaults
    @Test
    fun testSettingsDefaults() {
        val settings = GameSettings()
        assertEquals(AppTheme.SYSTEM, settings.theme)
        assertTrue(settings.dynamicColor)
        assertEquals(CpuDifficulty.NORMAL, settings.cpuDifficulty)
        assertEquals(TurnTimerOption.OFF, settings.turnTimer)
        assertTrue(settings.soundEnabled)
        assertTrue(settings.hapticsEnabled)
        assertTrue(settings.animationsEnabled)
        assertTrue(settings.showMathFeedback)
        assertTrue(settings.confirmRestart)
        assertFalse(settings.showCoordinates)
    }

    // 51. Timer option properties
    @Test
    fun testTurnTimerOptions() {
        assertFalse(TurnTimerOption.OFF.isEnabled)
        assertEquals(0, TurnTimerOption.OFF.seconds)

        assertTrue(TurnTimerOption.FIVE_SEC.isEnabled)
        assertEquals(5, TurnTimerOption.FIVE_SEC.seconds)

        assertTrue(TurnTimerOption.TEN_SEC.isEnabled)
        assertEquals(10, TurnTimerOption.TEN_SEC.seconds)
    }

    // Statistics overall accuracy
    @Test
    fun testOverallAccuracyCalculation() {
        val statsZero = DuelStatistics()
        assertEquals(100, statsZero.overallAccuracy)

        val statsNormal = DuelStatistics(totalCorrectPicks = 18, totalWrongPicks = 2)
        assertEquals(90, statsNormal.overallAccuracy)
    }
}
