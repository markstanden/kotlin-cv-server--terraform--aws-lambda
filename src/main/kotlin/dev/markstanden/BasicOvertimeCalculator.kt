package dev.markstanden

class BasicOvertimeCalculator : OvertimeCalculator, Executable {

	override fun execute(input: HandlerInput): HandlerOutput {
		return HandlerOutput(calcOffset(booked = input.booked ?: emptyList(), paid = input.paid ?: emptyList()))
	}

	override fun calcOffset(booked: List<Int>, paid: List<Int>): Int {
		return paid.sum() - booked.sum()
	}
}