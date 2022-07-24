package dev.markstanden

interface OvertimeCalculator {
	fun calcOffset(booked: List<Int>, paid: List<Int>): Int
}