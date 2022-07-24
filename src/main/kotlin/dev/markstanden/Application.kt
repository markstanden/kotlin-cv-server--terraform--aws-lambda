package dev.markstanden

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class Application : RequestHandler<HandlerInput, HandlerOutput> {
	private val calculator = BasicOvertimeCalculator()

	override fun handleRequest(input: HandlerInput?, context: Context?): HandlerOutput {
		input?.let {
			return HandlerOutput(it.booked, it.paid, calculator.calcOffset(it.booked, it.paid))
		}
		return HandlerOutput();
	}
}