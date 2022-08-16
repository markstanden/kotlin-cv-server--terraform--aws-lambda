package dev.markstanden


import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import dev.markstanden.Files.asResource
import dev.markstanden.models.CV
import dev.markstanden.models.CoverLetter
import kotlinx.serialization.*
import kotlinx.serialization.json.Json


class DataLookup : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	private val jsonParser = Json { ignoreUnknownKeys = true }

	override fun handleRequest(input: APIGatewayV2HTTPEvent?, context: Context?): APIGatewayV2HTTPResponse {

		// Obtain the path variable
		val path = input?.pathParameters?.get("version") ?: "default"
		//val bodyString = input?.body ?: ""

		// Get the logger from the lambda context and log the path attempt.
		context?.logger?.log("Access made with version: $path")

		// Use Kotlin's serialise to convert body string into a kotlin data object.
		//val body = jsonParser.decodeFromString<HandlerInput>(bodyString)

		val sampleCV = Json.decodeFromString(CV.serializer(), asResource(path = "/assets/sampleCV.json")!!)
		context?.logger?.log("Cover Letter: ${sampleCV.coverLetter}")

		// build a response
		val res = positiveResponse(sampleCV)
		context?.logger?.log("Response Body: ${res.body}")
		return res
	}

	private fun positiveResponse(
		bodyData: CV, contentType: String = "application/json",
	): APIGatewayV2HTTPResponse =
		APIGatewayV2HTTPResponse.builder()
			.withStatusCode(200)
			.withIsBase64Encoded(false)
			.withHeaders(mapOf("Content-Type" to contentType))
			.withBody(jsonParser.encodeToString(bodyData))
			.build()
}