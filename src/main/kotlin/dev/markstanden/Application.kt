package dev.markstanden

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.InputStream
import java.io.OutputStream


class Application : RequestStreamHandler {

	//	private val lambda: Executable = BasicOvertimeCalculator()
	private val mapper = jacksonObjectMapper()

	override fun handleRequest(input: InputStream?, output: OutputStream?, context: Context?) {
		input?.let { input ->
			val inputObj = mapper.readValue(input, HandlerInput::class.java)
			val result = HandlerOutput(difference = inputObj.paid.sum() - inputObj.booked.sum())

			context?.let { context ->
				val logger: LambdaLogger = context.logger
				logger.log("Input: ${input.readAllBytes().toString()}")
				logger.log("Booked: ${inputObj.booked}")
				logger.log("Paid: ${inputObj.paid}")
				logger.log("Result: $result")

			}

			mapper.writeValue(output, result)
		}
	}

//	override fun handleRequest(input: HandlerInput?, context: Context?): HandlerOutput {
//		input?.let { input ->
//			context?.let { context ->
//				val logger: LambdaLogger = context.logger
//				logger.log("Input: ${input.}")
//				logger.log("Booked: ${input.booked}")
//				logger.log("Paid: ${input.paid}")
//			}
//			return HandlerOutput(difference = input.paid.sum() - input.booked.sum())
//		}
//		return HandlerOutput()
//	}
}
//	override fun handleRequest(input: HandlerInput?, context: Context?): HandlerOutput {
//		input?.let {
//			return lambda.execute(it)
//		}
//		return HandlerOutput()
//	}