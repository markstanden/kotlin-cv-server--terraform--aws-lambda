package dev.markstanden

import kotlinx.serialization.Serializable

@Serializable
data class HandlerOutput(
	val difference: Int = 99999,
)