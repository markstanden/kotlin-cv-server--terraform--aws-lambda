package dev.markstanden


import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import dev.markstanden.aws_response.respondNotFound
import dev.markstanden.aws_response.respondOK
import dev.markstanden.datastore.DataStore
import dev.markstanden.datastore.GitHub
import dev.markstanden.environment.getGithubVariables
import dev.markstanden.files.asResource
import dev.markstanden.http.StatusCode
import dev.markstanden.models.CV
import dev.markstanden.models.ErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


class DataLookup : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	override fun handleRequest(input: APIGatewayV2HTTPEvent?, context: Context?): APIGatewayV2HTTPResponse {

		// create a logger
		val logger = context?.logger

		// Create an instance of the datastore
		val datastore: DataStore = GitHub(getGithubVariables(), logger)

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

			var ghRes: Pair<CV?, StatusCode>
			runBlocking {
				ghRes =
					withContext(Dispatchers.Default) {
						datastore.getCV(path)
					}
			}

			logger?.log(ghRes.toString())

			return if (ghRes.second == StatusCode.OK) respondOK(ghRes.first)
			else respondNotFound(ErrorMessage(error = "There was a problem, ${ghRes.second}"))
		}
		return respondNotFound(ErrorMessage(error = "Version not specified in url, default route not defined"))
	}

}