package dev.markstanden

class BasicOvertimeCalculator : OvertimeCalculator {
	override fun calcOffset(booked: List<Int>, paid: List<Int>): Int {
		return paid.sum() - booked.sum()
	}
}