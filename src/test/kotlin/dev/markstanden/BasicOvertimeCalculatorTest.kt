package dev.markstanden

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class BasicOvertimeCalculatorTest {

	@Test
	fun `Test that offset is 0 if paid exactly what was booked`() {
		val bookedOT = listOf(1, 2, 3, 4, 5, 6)
		val paidOT = listOf(1, 2, 3, 4, 5, 6)

		val sut = BasicOvertimeCalculator()
		val result = sut.calcOffset(booked = bookedOT, paid = paidOT)
		val expected = 0;

		assertEquals(expected, result, "Got paid what was booked, offset should be 0")
	}

	@Test
	fun `Test that offset is 1 if paid one more hour than was booked`() {
		val bookedOT = listOf(1, 2, 3, 4, 5, 6)
		val paidOT = listOf(2, 2, 3, 4, 5, 6)

		val sut = BasicOvertimeCalculator()
		val result = sut.calcOffset(booked = bookedOT, paid = paidOT)
		val expected = 1;

		assertEquals(expected, result, "Got paid more than was booked, offset should be 1")
	}

	@Test
	fun `Test that offset is -1 if paid one less hour than was booked`() {
		val bookedOT = listOf(1, 2, 3, 4, 5, 6)
		val paidOT = listOf(0, 2, 3, 4, 5, 6)

		val sut = BasicOvertimeCalculator()
		val result = sut.calcOffset(booked = bookedOT, paid = paidOT)
		val expected = -1;

		assertEquals(expected, result, "Got paid less than was booked, offset should be -1")
	}
}