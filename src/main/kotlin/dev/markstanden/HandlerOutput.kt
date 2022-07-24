package dev.markstanden

data class HandlerOutput(
	val booked: List<Int> = emptyList(), val paid: List<Int> = emptyList(), val difference: Int = 0,
)