package dev.markstanden

import dev.markstanden.models.CV
import dev.markstanden.models.TerraformOutputs
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertContains

class EndToEndTests {

	companion object {
		private var connectionResult: String = ""
		private val json = Json { ignoreUnknownKeys = true }
		private val terraformOutputs =
			json.decodeFromString(TerraformOutputs.serializer(), File("terraform.tfstate").readText()).outputs

		@JvmStatic
		@BeforeAll
		fun `Create a connection and return the output from the lambda`() {
			val baseUrl = terraformOutputs.base_url.value
			val version = "full"

			val connection = URL("$baseUrl/$version").openConnection() as HttpURLConnection
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