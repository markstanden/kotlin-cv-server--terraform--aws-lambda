package dev.markstanden

import kotlinx.serialization.Serializable

@Serializable
data class HandlerInput(
	val version: String = "",
)