package dev.markstanden.datastore

import com.amazonaws.services.lambda.runtime.LambdaLogger
import dev.markstanden.environment.GitHubIdentification
import dev.markstanden.http.StatusCode
import dev.markstanden.models.CV
import dev.markstanden.models.GitHubAPI
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

class GitHub(private val id: GitHubIdentification, private val logger: LambdaLogger? = null) : DataStore {


	override val baseBranch = "full"

	companion object {
		/** GitHub recommends the following accept string */
		private const val GITHUB_JSON = "application/vnd.github.v3+json"

		/** The name of the directory the cover letter and CV is held in */
		private const val BASE_DIRECTORY_NAME = "data"

		/** The filename of the CV file in the repo */
		private const val CV_FILENAME = "cv.json"

		/** creates a Json object for use by the class */
		private val json = Json { ignoreUnknownKeys = true }
	}

	/** Function to generate the URL for a particular file, from a particular branch */
	private val repoURL = urlGenerator(id.userName)(id.repoName)(BASE_DIRECTORY_NAME)


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


	override suspend fun getCV(id: String): Pair<CV?, StatusCode> {

		val fileContents = coroutineScope { getFile(branch = id) }

		if (fileContents.second == StatusCode.OK) {
			return fileContents
		}
		return Pair(null, fileContents.second)
	}

	private fun getFile(branch: String): Pair<CV?, StatusCode> {
		val url = repoURL(branch)(CV_FILENAME)

		val lookupResponse = try {
			json.decodeFromString<GitHubAPI.Contents>(get(id.personalAccessToken)(url))
		}
		catch (ex: Exception) {
			println(ex)
			return Pair(null, StatusCode.NotFound)
		}

		// GH response for a file lookup contains the file SHA and a direct download link.
		val repoData = try {
			get(id.personalAccessToken)(lookupResponse.download_url)
		}
		catch (ex: java.lang.Exception) {
			println(ex)
			return Pair(null, StatusCode.BadRequest)
		}
		return Pair(json.decodeFromString(repoData), StatusCode.OK)
	}

	private fun get(personalAccessToken: String) =
		{ url: String ->
			logger?.log("GitHub::get Creating connection for $url")
			val conn = URL(url).openConnection().apply {
				readTimeout = 1000
				connectTimeout = 1000
				setRequestProperty("Method", "POST")
				setRequestProperty("Accept", GITHUB_JSON)
				setRequestProperty("Authorization", "token $personalAccessToken")
			}
			// 8 Seconds later
			logger?.log("GitHub::get establishing connection for $url")
			val reader = conn.getInputStream().reader()
			logger?.log("GitHub::get reading from connection for $url")
			val result = reader.readText()
			logger?.log("GitHub::get reading complete for $url")
			result
		}
}