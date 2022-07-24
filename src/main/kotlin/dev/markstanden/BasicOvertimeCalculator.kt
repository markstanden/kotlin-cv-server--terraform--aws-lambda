package dev.markstanden

class BasicOvertimeCalculator : OvertimeCalculator, Executable {

	override fun execute(input: HandlerInput): HandlerOutput {
		return HandlerOutput(calcOffset(booked = input.booked, paid = input.paid))
	}

	override fun calcOffset(booked: List<Int>, paid: List<Int>): Int {
		return paid.sum() - booked.sum()
	}
}