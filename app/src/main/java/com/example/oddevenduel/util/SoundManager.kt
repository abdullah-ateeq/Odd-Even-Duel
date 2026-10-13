package com.example.oddevenduel.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

class SoundManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 65)
    } catch (_: Exception) {
        null
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    fun playSelect(enabled: Boolean) {
        if (!enabled) return
        try {
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.8f)
        } catch (_: Exception) {}
    }

    fun playCorrect(enabled: Boolean) {
        if (!enabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
        } catch (_: Exception) {}
    }

    fun playWrong(enabled: Boolean) {
        if (!enabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
        } catch (_: Exception) {}
    }

    fun playCpuClaim(enabled: Boolean) {
        if (!enabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 90)
        } catch (_: Exception) {}
    }

    fun playTurnChange(enabled: Boolean) {
        if (!enabled) return
        try {
            audioManager?.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_UP, 0.6f)
        } catch (_: Exception) {}
    }

    fun playVictory(enabled: Boolean) {
        if (!enabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 350)
        } catch (_: Exception) {}
    }

    fun playDefeat(enabled: Boolean) {
        if (!enabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 300)
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
