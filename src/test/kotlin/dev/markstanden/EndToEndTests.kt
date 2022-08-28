package dev.markstanden

import dev.markstanden.models.CV
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertContains

class EndToEndTests {

	companion object {
		private var connectionResult: String = ""
		private val json = Json { ignoreUnknownKeys = true }

		@JvmStatic
		@BeforeAll
		fun `Create a connection and return the output from the lambda`() {
			val baseUrl = "https://hxf4ajrje4.execute-api.eu-west-2.amazonaws.com"
			val stage = "cv-server_stage"
			val version = "full"

			val connection = URL("$baseUrl/$stage/$version").openConnection() as HttpURLConnection
			connection.apply {
				readTimeout = 0
				connectTimeout = 0
				requestMethod = "POST"
				setRequestProperty("Accept", "application/json")
			}

			connection.inputStream.bufferedReader().use { reader ->
				connectionResult = reader.readText()
			}
		}
	}

	@Test
	fun `Test output has a name`() {
		assertContains(connectionResult, "name")
	}

	@Test
	fun `Test output is a valid CV object`() {
		assertDoesNotThrow {
			json.decodeFromString(CV.serializer(), connectionResult)
		}
	}

	@Test
	fun `Test invalid CV object causes test failure`() {
		val invalid = "{$connectionResult}"
		assertThrows<Exception> {
			json.decodeFromString(CV.serializer(), invalid)
		}
	}
}