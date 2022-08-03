package dev.markstanden

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DataLookupTest {
	@Test
	fun `Test that correctly passed list is processed correctly`() {
		val inputString = """{"booked":[1,2,3],"paid":[1,2,3]}"""
		val input = APIGatewayV2HTTPEvent.builder().withBody(inputString).build()
		val outputString = """{"difference":0}"""
		val expected = APIGatewayV2HTTPResponse.builder()
			.withBody(outputString)
			.withIsBase64Encoded(false)
			.withStatusCode(200)
			.withHeaders(mapOf("Content-Type" to "application/json"))
			.build()

		val sut = DataLookup()
		val result = sut.handleRequest(input = input, context = null)

		assertEquals(expected, result)
	}

	@Test
	fun `Test that overpaid inputs produce a positive difference value`() {
		val inputString = """{"booked":[1,2,3],"paid":[2,2,3]}"""
		val input = APIGatewayV2HTTPEvent.builder().withBody(inputString).build()
		val outputString = """{"difference":1}"""
		val expected = APIGatewayV2HTTPResponse.builder()
			.withBody(outputString)
			.withIsBase64Encoded(false)
			.withStatusCode(200)
			.withHeaders(mapOf("Content-Type" to "application/json"))
			.build()

		val sut = DataLookup()
		val result = sut.handleRequest(input = input, context = null)

		assertEquals(expected, result)
	}

	@Test
	fun `Test that underpaid inputs produce a negative difference value`() {
		val inputString = """{"booked":[1,2,3],"paid":[0,2,3]}"""
		val input = APIGatewayV2HTTPEvent.builder().withBody(inputString).build()
		val outputString = """{"difference":-1}"""
		val expected = APIGatewayV2HTTPResponse.builder()
			.withBody(outputString)
			.withIsBase64Encoded(false)
			.withStatusCode(200)
			.withHeaders(mapOf("Content-Type" to "application/json"))
			.build()
		val sut = DataLookup()
		val result = sut.handleRequest(input = input, context = null)

		assertEquals(expected, result)
	}

//	@Test
//	fun `Test that empty input returns zero values`() {
//		val input = null
//		val outputString = """{"difference":99999}"""
//		val expected = APIGatewayV2HTTPEvent.builder().withBody(outputString).build()
//
//		val sut = Application()
//		val result = sut.handleRequest(input = input, context = null)
//
//		assertEquals(expected, result)
//	}
}