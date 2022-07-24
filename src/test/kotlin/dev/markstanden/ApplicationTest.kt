package dev.markstanden

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ApplicationTest {

	@Test
	fun `Test that correctly passed list is processed correctly`() {
		val input = HandlerInput()
		input.booked = listOf(1, 2, 3)
		input.paid = listOf(1, 2, 3)
		val expected = HandlerOutput(difference = 0);

		val sut = Application()
		val result = sut.handleRequest(input = input, context = null)

		assertEquals(expected, result)
	}

	@Test
	fun `Test that empty input returns zero values`() {
		val input = null
		val expected = HandlerOutput(difference = 0);

		val sut = Application()
		val result = sut.handleRequest(input = input, context = null)

		assertEquals(expected, result)
	}
}