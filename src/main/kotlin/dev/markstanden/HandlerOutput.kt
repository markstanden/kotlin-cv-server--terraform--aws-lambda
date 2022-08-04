package dev.markstanden

import dev.markstanden.models.CV
import dev.markstanden.models.CoverLetter
import kotlinx.serialization.Serializable

@Serializable
data class HandlerOutput(
	val coverLetter: CoverLetter?,
	val cv: CV?,
)