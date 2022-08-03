package dev.markstanden

import kotlinx.serialization.Serializable

@Serializable
data class HandlerInput(
	val booked: List<Int> = emptyList(),
	val paid: List<Int> = emptyList(),
)