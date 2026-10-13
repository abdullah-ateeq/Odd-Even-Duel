package com.example.oddevenduel.domain.model

enum class TurnTimerOption(
    val seconds: Int,
    val label: String
) {
    OFF(0, "Off"),
    FIVE_SEC(5, "5 seconds"),
    TEN_SEC(10, "10 seconds");

    val isEnabled: Boolean
        get() = seconds > 0
}
