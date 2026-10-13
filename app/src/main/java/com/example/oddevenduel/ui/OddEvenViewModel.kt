package com.example.oddevenduel.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oddevenduel.data.PreferencesManager
import com.example.oddevenduel.domain.logic.OddEvenCpuStrategy
import com.example.oddevenduel.domain.logic.OddEvenGameEngine
import com.example.oddevenduel.domain.model.DuelStatistics
import com.example.oddevenduel.domain.model.GamePhase
import com.example.oddevenduel.domain.model.GameResult
import com.example.oddevenduel.domain.model.GameSettings
import com.example.oddevenduel.domain.model.OddEvenGameState
import com.example.oddevenduel.util.HapticsManager
import com.example.oddevenduel.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class OddEvenViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application)
    private val soundManager = SoundManager(application)
    private val hapticsManager = HapticsManager(application)

    private val _settings = MutableStateFlow(prefsManager.loadSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    private val _statistics = MutableStateFlow(prefsManager.loadStatistics())
    val statistics: StateFlow<DuelStatistics> = _statistics.asStateFlow()

    private val _gameState = MutableStateFlow(OddEvenGameEngine.startNewGame())
    val gameState: StateFlow<OddEvenGameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null
    private var cpuTurnJob: Job? = null
    private var isProcessingMove = false

    init {
        startTurnTimerIfNeeded()
    }

    fun startNewGame() {
        timerJob?.cancel()
        cpuTurnJob?.cancel()
        isProcessingMove = false

        _gameState.value = OddEvenGameEngine.startNewGame()
        startTurnTimerIfNeeded()
    }

    fun selectCell(cellId: Int) {
        val state = _gameState.value
        // Rapid tap protection: ignore if not player's turn or already processing
        if (state.phase != GamePhase.PLAYER_TURN || isProcessingMove) return

        val cell = state.cells.find { it.id == cellId } ?: return
        if (!cell.isActive) return

        isProcessingMove = true
        timerJob?.cancel()

        val currentSettings = _settings.value
        soundManager.playSelect(currentSettings.soundEnabled)

        val newState = OddEvenGameEngine.processPlayerSelection(
            currentState = state,
            cellId = cellId,
            showMathFeedback = currentSettings.showMathFeedback,
            random = Random
        )
        _gameState.value = newState

        // Audio & Haptic feedback
        if (newState.lastSelectedCorrect == true) {
            soundManager.playCorrect(currentSettings.soundEnabled)
            hapticsManager.vibrateCorrect(currentSettings.hapticsEnabled)
        } else {
            soundManager.playWrong(currentSettings.soundEnabled)
            hapticsManager.vibrateWrong(currentSettings.hapticsEnabled)
        }

        if (newState.phase == GamePhase.GAME_OVER) {
            handleGameOver(newState)
            isProcessingMove = false
        } else {
            // Trigger CPU turn after brief animation/transition delay
            viewModelScope.launch {
                if (currentSettings.animationsEnabled) {
                    delay(450)
                } else {
                    delay(150)
                }
                // Clear animation highlight
                _gameState.update { it.copy(animatingCellId = null, shakeCellId = null) }
                isProcessingMove = false
                triggerCpuTurn()
            }
        }
    }

    private fun triggerCpuTurn() {
        cpuTurnJob?.cancel()
        cpuTurnJob = viewModelScope.launch {
            val stateBeforeDelay = _gameState.value
            if (stateBeforeJobValid(stateBeforeDelay)) {
                val currentSettings = _settings.value
                soundManager.playTurnChange(currentSettings.soundEnabled)

                // 300-700ms CPU thinking delay
                val thinkDelay = (400L..650L).random()
                delay(thinkDelay)

                val currentState = _gameState.value
                if (currentState.phase != GamePhase.CPU_TURN) return@launch

                val cpuChoice = OddEvenCpuStrategy.selectCell(
                    cells = currentState.cells,
                    challenge = currentState.challenge,
                    difficulty = currentSettings.cpuDifficulty,
                    random = Random
                )

                if (cpuChoice != null) {
                    val afterCpuState = OddEvenGameEngine.processCpuSelection(
                        currentState = currentState,
                        selectedCell = cpuChoice,
                        showMathFeedback = currentSettings.showMathFeedback,
                        random = Random
                    )
                    _gameState.value = afterCpuState

                    if (afterCpuState.lastSelectedCorrect == true) {
                        soundManager.playCpuClaim(currentSettings.soundEnabled)
                        hapticsManager.vibrateCpuClaim(currentSettings.hapticsEnabled)
                    } else {
                        soundManager.playWrong(currentSettings.soundEnabled)
                    }

                    if (afterCpuState.phase == GamePhase.GAME_OVER) {
                        handleGameOver(afterCpuState)
                    } else {
                        if (currentSettings.animationsEnabled) {
                            delay(400)
                        } else {
                            delay(100)
                        }
                        _gameState.update { it.copy(animatingCellId = null, shakeCellId = null) }
                        soundManager.playTurnChange(currentSettings.soundEnabled)
                        startTurnTimerIfNeeded()
                    }
                }
            }
        }
    }

    private fun stateBeforeJobValid(state: OddEvenGameState): Boolean {
        return state.phase == GamePhase.CPU_TURN
    }

    private fun startTurnTimerIfNeeded() {
        timerJob?.cancel()
        val timerOption = _settings.value.turnTimer
        if (!timerOption.isEnabled || _gameState.value.phase != GamePhase.PLAYER_TURN) {
            _gameState.update { it.copy(turnTimeRemaining = null) }
            return
        }

        timerJob = viewModelScope.launch {
            var remaining = timerOption.seconds
            _gameState.update { it.copy(turnTimeRemaining = remaining) }

            while (remaining > 0) {
                delay(1000)
                remaining--
                if (_gameState.value.phase != GamePhase.PLAYER_TURN) break
                _gameState.update { it.copy(turnTimeRemaining = remaining) }
            }

            if (remaining <= 0 && _gameState.value.phase == GamePhase.PLAYER_TURN) {
                // Timeout occurred
                val currentSettings = _settings.value
                soundManager.playWrong(currentSettings.soundEnabled)
                hapticsManager.vibrateWrong(currentSettings.hapticsEnabled)

                val timeoutState = OddEvenGameEngine.processTimeout(
                    currentState = _gameState.value,
                    random = Random
                )
                _gameState.value = timeoutState
                triggerCpuTurn()
            }
        }
    }

    private fun handleGameOver(finalState: OddEvenGameState) {
        timerJob?.cancel()
        val result = finalState.result ?: return
        val currentSettings = _settings.value

        when (result) {
            GameResult.PLAYER_WIN, GameResult.PERFECT_DUEL -> {
                soundManager.playVictory(currentSettings.soundEnabled)
                hapticsManager.vibrateVictory(currentSettings.hapticsEnabled)
            }
            GameResult.CPU_WIN -> {
                soundManager.playDefeat(currentSettings.soundEnabled)
                hapticsManager.vibrateDefeat(currentSettings.hapticsEnabled)
            }
            GameResult.DRAW -> {
                soundManager.playTurnChange(currentSettings.soundEnabled)
            }
        }

        val updatedStats = prefsManager.recordGameResult(
            result = result,
            playerScore = finalState.playerScore,
            playerCells = finalState.playerCells,
            playerAccuracy = finalState.playerAccuracy,
            correctPicks = finalState.playerCorrectSelections,
            wrongPicks = finalState.playerIncorrectSelections,
            timeouts = finalState.playerTimeouts
        )
        _statistics.value = updatedStats
    }

    fun updateSettings(newSettings: GameSettings) {
        prefsManager.saveSettings(newSettings)
        _settings.value = newSettings
        if (_gameState.value.phase == GamePhase.PLAYER_TURN) {
            startTurnTimerIfNeeded()
        }
    }

    fun resetSettings() {
        val defaultSettings = prefsManager.resetSettings()
        _settings.value = defaultSettings
        if (_gameState.value.phase == GamePhase.PLAYER_TURN) {
            startTurnTimerIfNeeded()
        }
    }

    fun resetStatistics() {
        val freshStats = prefsManager.resetStatistics()
        _statistics.value = freshStats
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        cpuTurnJob?.cancel()
        soundManager.release()
    }
}
