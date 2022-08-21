package dev.markstanden

import kotlinx.serialization.Serializable

@Serializable
data class Handler_Input(
	val version: String = "",
)