package dev.markstanden.models

import kotlinx.serialization.Serializable

@Serializable
data class TerraformOutputs(val outputs: TerraformOutput) {

	@Serializable
	data class TerraformOutput(val base_url: BaseUrl) {

		@Serializable
		data class BaseUrl(val value: String)
	}
}