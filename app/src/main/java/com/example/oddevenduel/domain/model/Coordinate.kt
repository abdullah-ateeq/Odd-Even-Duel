package com.example.oddevenduel.domain.model

data class Coordinate(
    val row: Int,
    val column: Int
) {
    fun toLabel(): String {
        val colLetter = ('A' + column)
        val rowNumber = row + 1
        return "$colLetter$rowNumber"
    }
}
