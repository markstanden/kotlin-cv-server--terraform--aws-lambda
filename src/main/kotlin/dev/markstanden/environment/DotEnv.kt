package dev.markstanden.environment

import io.github.cdimascio.dotenv.dotenv

/**
 * Wraps the external dependency providing environment variables
 */
class DotEnv : FromEnvironment {
	override fun get(key: EnvironmentVariables) =
		dotenv { ignoreIfMissing = true }[key.name] ?: ""
}