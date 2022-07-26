package dev.markstanden

data class HandlerInput(
	val booked: List<Int> = emptyList(),
	val paid: List<Int> = emptyList(),
)