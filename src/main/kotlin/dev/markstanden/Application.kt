package dev.markstanden

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class Application : RequestHandler<HandlerInput, HandlerOutput> {

	private val lambda: Executable = BasicOvertimeCalculator()

	override fun handleRequest(input: HandlerInput?, context: Context?): HandlerOutput {
		input?.let {
			return lambda.execute(input)

		}
		return HandlerOutput();
	}
}