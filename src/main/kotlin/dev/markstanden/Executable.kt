package dev.markstanden

interface Executable {
	fun execute(input: HandlerInput): HandlerOutput
}