package dev.markstanden


import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import dev.markstanden.aws_response.respondNotFound
import dev.markstanden.aws_response.respondOK
import dev.markstanden.datastore.DataStore
import dev.markstanden.datastore.GitHub
import dev.markstanden.files.asResource
import dev.markstanden.http.StatusCode
import dev.markstanden.models.CV
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


class DataLookup : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	override fun handleRequest(input: APIGatewayV2HTTPEvent?, context: Context?): APIGatewayV2HTTPResponse {

		// create a logger
		val logger = context?.logger

		// Create an instance of the datastore
		val datastore: DataStore = GitHub(logger)

		// Obtain the path variable
		val path = input?.pathParameters?.get("version")
		//val bodyString = input?.body ?: ""

		// Use Kotlin's serialise to convert body string into a kotlin data object.
		//val body = jsonParser.decodeFromString<Handler_Input>(bodyString)

		path?.apply {
			// Get the logger from the lambda context and log the path attempt.
			logger?.log("Access made with version: $path")

			// Send the local sample
			if (path == "sample") {
				val cv = Json.decodeFromString(CV.serializer(), asResource(path = "/assets/sampleCV.json")!!)
				return respondOK(cv)
			}

			val ghRes: Pair<CV?, StatusCode> = runBlocking {
				logger?.log("coroutine begun ok")
				datastore.getCV(path)
			}
			logger?.log("coroutine ends")

			logger?.log(ghRes.toString())

			if (ghRes.second == StatusCode.OK) {
				return respondOK(ghRes.first)
			}
		}
		return respondNotFound("""{"error":"Version not specified in url, default route not defined"}""")
	}

}