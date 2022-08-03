package dev.markstanden

fun calcOffset(booked: List<Int>, paid: List<Int>): Int {
	return paid.sum() - booked.sum()
}