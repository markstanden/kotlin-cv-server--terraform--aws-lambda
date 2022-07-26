//package dev.markstanden
//
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import org.junit.jupiter.api.Test
//
//import org.junit.jupiter.api.Assertions.*
//
//internal class ApplicationTest {
//	private val mapper = jacksonObjectMapper()
//	@Test
//	fun `Test that correctly passed list is processed correctly`() {
//		val input = HandlerInput(listOf(1, 2, 3), listOf(1, 2, 3))
//		val expected = HandlerOutput(difference = 0)
//
//		val sut = Application()
//		val result = sut.handleRequest(input = input, context = null)
//
//		assertEquals(expected, result)
//	}
//
//	@Test
//	fun `Test that overpaid inputs produce a positive difference value`() {
//		val input = HandlerInput()
//		input.booked = listOf(1, 2, 3)
//		input.paid = listOf(2, 2, 3)
//		val expected = HandlerOutput(difference = 1)
//
//		val sut = Application()
//		val result = sut.handleRequest(input = input, context = null)
//
//		assertEquals(expected, result)
//	}
//
//	@Test
//	fun `Test that underpaid inputs produce a negative difference value`() {
//		val input = HandlerInput()
//		input.booked = listOf(1, 2, 3)
//		input.paid = listOf(0, 2, 3)
//		val expected = HandlerOutput(difference = -1)
//
//		val sut = Application()
//		val result = sut.handleRequest(input = input, context = null)
//
//		assertEquals(expected, result)
//	}
//
//	@Test
//	fun `Test that empty input returns zero values`() {
//		val input = null
//		val expected = HandlerOutput(difference = 99999)
//
//		val sut = Application()
//		val result = sut.handleRequest(input = input, context = null)
//
//		assertEquals(expected, result)
//	}
//}