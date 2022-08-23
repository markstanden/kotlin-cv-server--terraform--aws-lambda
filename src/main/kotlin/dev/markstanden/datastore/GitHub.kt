package dev.markstanden.datastore

import dev.markstanden.environment.getGithubVariables
import dev.markstanden.models.CV
import dev.markstanden.models.GitHubAPI
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.net.URL
import com.amazonaws.services.lambda.runtime.LambdaLogger
import dev.markstanden.http.StatusCode
import kotlin.reflect.typeOf

class GitHub(private val logger: LambdaLogger? = null) : DataStore {

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

	override suspend fun getCV(id: String): Pair<CV?, StatusCode> {
		logger?.log("GitHub::getCV called with id \n $id")

		val fileContents = getFile(branch = id)

		logger?.log("results returned from GitHub::getFile \n $fileContents")

		if (fileContents.second == StatusCode.OK) {
			return fileContents
		}
		return Pair(null, fileContents.second)
	}

	private fun getFile(branch: String): Pair<CV?, StatusCode> {
		logger?.log("GitHub::getFile called \n branch: $branch, fileName: $CV_FILENAME")

		val url = repoURL(branch)(CV_FILENAME)

		val lookupResponse = try {
			get<GitHubAPI.Contents>(env.personalAccessToken)(url)
		}
		catch (ex: Exception) {
			println(ex)
			return Pair(null, StatusCode.NotFound)
		}

		logger?.log("result of initial lookup within GitHub::getFile \n $lookupResponse")


		// GH response for a file lookup contains the file SHA and a direct download link.
		val repoData = try {
			get<CV>(env.personalAccessToken)(lookupResponse.download_url)
		}
		catch (ex: java.lang.Exception) {
			println(ex)
			return Pair(null, StatusCode.BadRequest)
		}
		logger?.log("repodata results returned from second lookup within GitHub::getFile \n $repoData")
		return Pair(repoData, StatusCode.OK)
	}

	@OptIn(ExperimentalSerializationApi::class)
	private inline fun <reified T> get(personalAccessToken: String) =
		{ url: String ->
			logger?.log("GitHub::get called \n personalAccessToken: $personalAccessToken \n url: $url")
			URL(url).openConnection().apply {
				readTimeout = 0
				connectTimeout = 0
				setRequestProperty("Accept", GITHUB_JSON)
				setRequestProperty("Authorization", "token $personalAccessToken")
				logger?.log("GitHub::getFile called \n ${this}")
			}.getInputStream().use {
				logger?.log("about to decode the following \n $it \n which has type: ${typeOf<T>()}")
				println("about to decode the following \n $it \n which has type: ${typeOf<T>()}")
				json.decodeFromStream<T>(it)
			}
		}
}