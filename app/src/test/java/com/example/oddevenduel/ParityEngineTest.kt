package com.example.oddevenduel

import com.example.oddevenduel.domain.logic.ParityEngine
import com.example.oddevenduel.domain.model.NumberParity
import com.example.oddevenduel.domain.model.ParityChallenge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ParityEngineTest {

    // 1. isOdd
    @Test
    fun testIsOdd() {
        assertTrue(ParityEngine.isOdd(1))
        assertTrue(ParityEngine.isOdd(3))
        assertTrue(ParityEngine.isOdd(17))
        assertTrue(ParityEngine.isOdd(49))
        assertFalse(ParityEngine.isOdd(2))
        assertFalse(ParityEngine.isOdd(4))
        assertFalse(ParityEngine.isOdd(50))
    }

    // 2. isEven
    @Test
    fun testIsEven() {
        assertTrue(ParityEngine.isEven(2))
        assertTrue(ParityEngine.isEven(4))
        assertTrue(ParityEngine.isEven(24))
        assertTrue(ParityEngine.isEven(50))
        assertFalse(ParityEngine.isEven(1))
        assertFalse(ParityEngine.isEven(3))
        assertFalse(ParityEngine.isEven(49))
    }

    // 3. getParity
    @Test
    fun testGetParity() {
        assertEquals(NumberParity.ODD, ParityEngine.getParity(1))
        assertEquals(NumberParity.EVEN, ParityEngine.getParity(2))
        assertEquals(NumberParity.ODD, ParityEngine.getParity(17))
        assertEquals(NumberParity.EVEN, ParityEngine.getParity(24))
    }

    // 4. 1 odd
    @Test
    fun testNumber1IsOdd() {
        assertTrue(ParityEngine.isOdd(1))
        assertFalse(ParityEngine.isEven(1))
        assertEquals(NumberParity.ODD, ParityEngine.getParity(1))
        assertTrue(ParityEngine.matchesChallenge(1, ParityChallenge.ODD))
        assertFalse(ParityEngine.matchesChallenge(1, ParityChallenge.EVEN))
    }

    // 5. 2 even
    @Test
    fun testNumber2IsEven() {
        assertFalse(ParityEngine.isOdd(2))
        assertTrue(ParityEngine.isEven(2))
        assertEquals(NumberParity.EVEN, ParityEngine.getParity(2))
        assertTrue(ParityEngine.matchesChallenge(2, ParityChallenge.EVEN))
        assertFalse(ParityEngine.matchesChallenge(2, ParityChallenge.ODD))
    }

    // 6. 3 odd
    @Test
    fun testNumber3IsOdd() {
        assertTrue(ParityEngine.isOdd(3))
        assertFalse(ParityEngine.isEven(3))
        assertEquals(NumberParity.ODD, ParityEngine.getParity(3))
    }

    // 7. 4 even
    @Test
    fun testNumber4IsEven() {
        assertFalse(ParityEngine.isOdd(4))
        assertTrue(ParityEngine.isEven(4))
        assertEquals(NumberParity.EVEN, ParityEngine.getParity(4))
    }

    // 8. 49 odd
    @Test
    fun testNumber49IsOdd() {
        assertTrue(ParityEngine.isOdd(49))
        assertFalse(ParityEngine.isEven(49))
        assertEquals(NumberParity.ODD, ParityEngine.getParity(49))
        assertTrue(ParityEngine.matchesChallenge(49, ParityChallenge.ODD))
        assertFalse(ParityEngine.matchesChallenge(49, ParityChallenge.EVEN))
    }

    // 9. 50 even
    @Test
    fun testNumber50IsEven() {
        assertFalse(ParityEngine.isOdd(50))
        assertTrue(ParityEngine.isEven(50))
        assertEquals(NumberParity.EVEN, ParityEngine.getParity(50))
        assertTrue(ParityEngine.matchesChallenge(50, ParityChallenge.EVEN))
        assertFalse(ParityEngine.matchesChallenge(50, ParityChallenge.ODD))
    }

    // 10. Challenge matching
    @Test
    fun testChallengeMatching() {
        assertTrue(ParityEngine.matchesChallenge(17, ParityChallenge.ODD))
        assertFalse(ParityEngine.matchesChallenge(17, ParityChallenge.EVEN))

        assertTrue(ParityEngine.matchesChallenge(24, ParityChallenge.EVEN))
        assertFalse(ParityEngine.matchesChallenge(24, ParityChallenge.ODD))
    }

    // 23. Score calculation
    @Test
    fun testScoreCalculation() {
        // 10 + (number / 10)
        assertEquals(10, ParityEngine.calculateTileScore(1))
        assertEquals(10, ParityEngine.calculateTileScore(7))
        assertEquals(10, ParityEngine.calculateTileScore(8))
        assertEquals(11, ParityEngine.calculateTileScore(12))
        assertEquals(11, ParityEngine.calculateTileScore(14))
        assertEquals(11, ParityEngine.calculateTileScore(19))
        assertEquals(12, ParityEngine.calculateTileScore(23))
        assertEquals(12, ParityEngine.calculateTileScore(27))
        assertEquals(13, ParityEngine.calculateTileScore(35))
        assertEquals(13, ParityEngine.calculateTileScore(38))
        assertEquals(14, ParityEngine.calculateTileScore(47))
        assertEquals(14, ParityEngine.calculateTileScore(49))
        assertEquals(15, ParityEngine.calculateTileScore(50))
    }
}
