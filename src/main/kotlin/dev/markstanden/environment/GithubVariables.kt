package dev.markstanden.environment

data class GitHubIdentification(val userName: String, val repoName: String, val personalAccessToken: String)

fun getGithubVariables(): GitHubIdentification {
	return GitHubIdentification(
		userName = DotEnv().get(EnvironmentVariables.USER_NAME),
		repoName = DotEnv().get(EnvironmentVariables.REPO_NAME),
		personalAccessToken = DotEnv().get(EnvironmentVariables.PERSONAL_ACCESS_TOKEN),
	)
}