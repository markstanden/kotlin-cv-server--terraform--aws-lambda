package dev.markstanden

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import io.ktor.http.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Ignore
import kotlin.test.assertContains

internal class DataLookupTest {
	@Test
	fun `Test that function returns correct basic response headers`() {
		val inputString = """{"test":"Shouldn't Matter"}"""
		val path = "sample"
		val input =
			APIGatewayV2HTTPEvent.builder().withBody(inputString).withPathParameters(mapOf("version" to path)).build()
		val expected =
			APIGatewayV2HTTPResponse.builder()
				.withIsBase64Encoded(false)
				.withStatusCode(200)
				.withHeaders(mapOf("Content-Type" to "application/json"))
				.build()

		val sut = DataLookup()
		val result = sut.handleRequest(input = input, context = null)

		assertEquals(expected.cookies, result.cookies)
		assertEquals(expected.headers, result.headers)
		assertEquals(expected.statusCode, result.statusCode)
		assertEquals(expected.isBase64Encoded, result.isBase64Encoded)
	}

	@Test
	fun `Test that handler returns sample correctly`() {
		val inputString = """{"test":"Shouldn't Matter"}"""
		val path = "sample"

		val input =
			APIGatewayV2HTTPEvent.builder().withBody(inputString).withPathParameters(mapOf("version" to path)).build()

		val sut = DataLookup()
		val expected = listOf<String>(
			"name", "First Second", "location", "city", "City", "country", "Country", "contact", "phone",
			"01234 567890", "email", "email@address.com"
		)
		val result = sut.handleRequest(input = input, context = null)

		expected.forEach { assertContains(result.body, it, message = "test word '$it' not found within result") }
	}

	@Test
	fun `Test that handler returns error if default path is attempted`() {
		val inputString = """{"test":"Shouldn't Matter"}"""
		val path = null

		val input =
			APIGatewayV2HTTPEvent.builder().withBody(inputString).withPathParameters(mapOf("version" to path)).build()

		val sut = DataLookup()
		val result = sut.handleRequest(input = input, context = null)
		assertFalse(result.statusCode == HttpStatusCode.OK.value)
	}
}