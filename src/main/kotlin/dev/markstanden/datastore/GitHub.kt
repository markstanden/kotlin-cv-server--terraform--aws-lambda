package dev.markstanden.datastore

import dev.markstanden.environment.getGithubVariables
import dev.markstanden.models.CV
import dev.markstanden.models.GitHubAPI
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.net.URL
import java.net.http.HttpResponse

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

		if (fileContents.second == HttpStatusCode.OK) {
			return fileContents
		}
		return Pair(null, fileContents.second)
	}

	private suspend fun getFile(branch: String, fileName: String): Pair<CV?, HttpStatusCode> {
		val url = repoURL(branch)(fileName)

		val lookupResponse = try {
			get<GitHubAPI.Contents>(env.personalAccessToken)(url)
		}
		catch (ex: Exception) {
			println(ex)
			return Pair(null, HttpStatusCode.NotFound)
		}

		// GH response for a file lookup contains the file SHA and a direct download link.
		val repoData = try {
			get<CV>(env.personalAccessToken)(lookupResponse.download_url)
		}
		catch (ex: java.lang.Exception) {
			println(ex)
			return Pair(null, HttpStatusCode.BadRequest)
		}
		return Pair(repoData, HttpStatusCode.OK)
	}

	@OptIn(ExperimentalSerializationApi::class)
	private inline fun <reified T> get(personalAccessToken: String) =
		{ url: String ->
			URL(url).openConnection().apply {
				readTimeout = 800
				connectTimeout = 200
				setRequestProperty("Accept", GITHUB_JSON)
				setRequestProperty("Authorization", "token $personalAccessToken")
			}.getInputStream().use {
				json.decodeFromStream<T>(it)
			}
		}
}