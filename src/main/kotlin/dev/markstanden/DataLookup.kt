package dev.markstanden


import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import dev.markstanden.aws_response.respondNotFound
import dev.markstanden.aws_response.respondOK
import dev.markstanden.files.asResource
import dev.markstanden.models.CV
import kotlinx.serialization.json.Json


class DataLookup : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	override fun handleRequest(input: APIGatewayV2HTTPEvent?, context: Context?): APIGatewayV2HTTPResponse {

		// Obtain the path variable
		val path = input?.pathParameters?.get("version")
		//val bodyString = input?.body ?: ""

		// Get the logger from the lambda context and log the path attempt.
		context?.logger?.log("Access made with version: $path")

		// Use Kotlin's serialise to convert body string into a kotlin data object.
		//val body = jsonParser.decodeFromString<Handler_Input>(bodyString)

		path?.apply {
			val cv = Json.decodeFromString(CV.serializer(), asResource(path = "/assets/sampleCV.json")!!)
			// build a response
			return respondOK(cv)
		}
		return respondNotFound("""{"error":"Version not specified in url, default route not defined"}""")

	}

}