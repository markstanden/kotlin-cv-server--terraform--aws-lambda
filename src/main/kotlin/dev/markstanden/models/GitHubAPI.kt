package dev.markstanden.models

import kotlinx.serialization.Serializable


@Serializable
class GitHubAPI {
	@Serializable
	data class Contents(
		/** The SHA hash representing the file */
		val sha: String,
		/** The direct download link for the file */
		val download_url: String,
	)
}