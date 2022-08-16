package dev.markstanden.datastore

import dev.markstanden.environment.getGithubVariables
import dev.markstanden.models.CV
import dev.markstanden.models.GitHubAPI
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class GitHub : DataStore {

	override val baseBranch = "full"

	companion object {
		/** GitHub recommends the following accept string */
		private const val GITHUB_JSON = "application/vnd.github.v3+json"

		/** The name of the directory the cover letter and CV is held in */
		private const val BASE_DIRECTORY_NAME = "data"

		/** The filename of the CV file in the repo */
		private const val CV_FILENAME = "cv.json"

		/** The filename of the cover letter file in the repo */
		private const val COVER_LETTER_FILENAME = "coverletter.json"

		/** creates a Json object for use by the class */
		private val json = Json { ignoreUnknownKeys = true }

		/** Gets the private environment variables required for the API connection */
		private val env = getGithubVariables()

		/** Function to generate the URL for a particular file, from a particular branch */
		private val repoURL = urlGenerator(env.userName)(env.repoName)(BASE_DIRECTORY_NAME)


		/**
		 * Generates the URL string to retrieve the file from GitHub
		 */
		private fun urlGenerator(userName: String) =
			{ repoName: String ->
				{ baseDir: String ->
					{ branch: String ->
						{ filename: String ->
							"https://api.github.com/repos/$userName/$repoName/contents/${if (baseDir != "") "$baseDir/" else ""}$filename?ref=$branch"
						}
					}
				}
			}
	}

	override suspend fun getCV(id: String): Pair<CV?, HttpStatusCode> {
		val fileContents = getFile(branch = id, fileName = CV_FILENAME)

		// The raw file downloaded, parse to a CV object
		val cv = json.decodeFromString<CV>(fileContents.body())

		return Pair(cv, HttpStatusCode.OK)
	}


	override suspend fun getCover(id: String): Pair<String, HttpStatusCode> {
		val fileContents = getFile(branch = id, fileName = COVER_LETTER_FILENAME)
		return Pair(fileContents.body(), HttpStatusCode.OK)
	}


	private suspend fun getFile(branch: String, fileName: String): HttpResponse {
		val client = HttpClient(CIO)
		val getWithAuthorization = get(client)(env.personalAccessToken)
		val url = repoURL(branch)(fileName)
		val lookupResponse = getWithAuthorization(url)
		// Abort early if the file is not found or inaccessible
		if (lookupResponse.status != HttpStatusCode.OK) {

			client.close()
			return lookupResponse
		}

		// GH response for a file lookup contains the file SHA and a direct download link.
		val fileInfo = json.decodeFromString<GitHubAPI.Contents>(lookupResponse.body())

		return getWithAuthorization(fileInfo.download_url)
	}

	private fun get(client: HttpClient) =
		{ personalAccessToken: String ->
			{ url: String ->
				runBlocking {
					client.get(url) {
						headers["Accept"] = GITHUB_JSON
						headers["Authorization"] = "token $personalAccessToken"
					}
				}
			}
		}
}