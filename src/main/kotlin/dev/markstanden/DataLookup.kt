package dev.markstanden


import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.Json


class DataLookup : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	private val jsonParser = Json { ignoreUnknownKeys = true }

	override fun handleRequest(input: APIGatewayV2HTTPEvent?, context: Context?): APIGatewayV2HTTPResponse {

		// Obtain the path variable
		val path = input?.pathParameters?.get("version") ?: "default"
		val bodyString = input?.body ?: ""

		// Get the logger from the lambda context and log the path attempt.
		context?.logger?.log("Access made with version: $path")

		// Use Kotlin's serialise to convert body string into a kotlin data object.
		val body = jsonParser.decodeFromString<HandlerInput>(bodyString)

		val result = calcOffset(body.booked, body.paid)

		// build a response
		return positiveResponse("""{"difference":${result}}""")
	}

	private fun positiveResponse(
		body: String = "", contentType: String = "application/json",
	): APIGatewayV2HTTPResponse =
		APIGatewayV2HTTPResponse.builder()
			.withStatusCode(200)
			.withIsBase64Encoded(false)
			.withHeaders(mapOf("Content-Type" to contentType))
			.withBody(body)
			.build()
}