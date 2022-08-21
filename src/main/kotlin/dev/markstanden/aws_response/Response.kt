package dev.markstanden.aws_response

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val jsonParser = Json { ignoreUnknownKeys = true }

inline fun <reified T> respondOK(bodyData: T) =
	jsonResponse(HttpStatusCode.OK, bodyData)

inline fun <reified T> respondNotFound(bodyData: T) =
	jsonResponse(HttpStatusCode.NotFound, bodyData)

inline fun <reified T> jsonResponse(statusCode: HttpStatusCode, bodyData: T) =
	response(statusCode, bodyData = jsonParser.encodeToString(bodyData), contentType = "application/json")

fun response(
	statusCode: HttpStatusCode, bodyData: String, contentType: String = "application/json",
): APIGatewayV2HTTPResponse =
	APIGatewayV2HTTPResponse.builder()
		.withStatusCode(statusCode.value)
		.withIsBase64Encoded(false)
		.withHeaders(mapOf("Content-Type" to contentType))
		.withBody(bodyData)
		.build()