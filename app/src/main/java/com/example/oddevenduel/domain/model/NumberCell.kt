package com.example.oddevenduel.domain.model

data class NumberCell(
    val id: Int,
    val coordinate: Coordinate,
    val value: Int,
    val state: CellState
) {
    val isActive: Boolean
        get() = state == CellState.ACTIVE

    val isClaimed: Boolean
        get() = state != CellState.ACTIVE
}
