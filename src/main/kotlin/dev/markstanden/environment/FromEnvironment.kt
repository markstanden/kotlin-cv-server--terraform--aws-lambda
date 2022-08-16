package dev.markstanden.environment

interface FromEnvironment {
	fun get(key: EnvironmentVariables): String
}